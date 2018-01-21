/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.DataValidationException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.datasource.ListDatasource;
import segmail.program.list.FormListDatasource;
import segmail.program.list.ProgramList;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSetupDatasource")
public class FormSetupDatasource {
    @Inject ProgramSubscribers program;
    @Inject FormListDatasource formDatasource;
    
    private long selectedListId;
    
    @EJB ListService listService;
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    
    @PostConstruct
    public void init() {
        
    }

    public long getSelectedListId() {
        return selectedListId;
    }

    public void setSelectedListId(long selectedListId) {
        this.selectedListId = selectedListId;
    }
    
    public List<SubscriptionList> getOwnedLists() {
        return program.getOwnedLists();
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        program.setOwnedLists(ownedLists);
    }
    
    public void setupDatasource() {
        try {
            // Activate datasync for this list
            SubscriptionList list = getOwnedLists().stream()
                    .filter(l -> l.getOBJECTID() == this.getSelectedListId())
                    .findFirst().get();
            List<ListDatasource> datasources = objService.getEnterpriseData(list.getOBJECTID(), ListDatasource.class);
            
            //If no existing, instantiate a new one
            ListDatasource ds;
            if(datasources == null || datasources.isEmpty()) {
                ds = new ListDatasource();
                ds.setOWNER(list);
                ds.setACTIVE(true);
                updService.persist(ds);
            } else {
                ds = datasources.get(0);
                ds.setACTIVE(true);
                ds = (ListDatasource) updService.merge(ds);
            }
            
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/list/" + getSelectedListId() + "/datasource");
        } catch (IOException ex) {
            Logger.getLogger(FormSetupDatasource.class.getName()).log(Level.SEVERE, "", ex);
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), "");
        }
    }
}
