/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.client.ClientService;
import segmail.entity.subscription.SubscriptionList;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormEditListHeader")
@RequestScoped
public class FormEditListHeader {
    @Inject
    private ProgramList programList;

    @EJB
    private ClientService clientService;
    @EJB
    private GenericObjectService genericDBService;

    public void makeLocal() {
        try {

        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public void makeRemote() {
        try {

        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public boolean isLoaded() {
        return !(this.programList.getListEditing() == null);
    }

    public SubscriptionList getLoadedList() {
        return (this.isLoaded()) ? this.getLoadedList() : null;
    }

    
}
