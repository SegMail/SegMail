/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormImportSubscriber")
public class FormImportSubscriber {
    
    @Inject ProgramList program;
    
    @EJB SubscriptionService subService;
    
    // File upload
    private Part file;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            initMapping();
        }
    }

    public Map<Integer, String> getListFieldMapping() {
        return program.getListFieldMapping();
    }

    public void setListFieldMapping(Map<Integer, String> listFieldMapping) {
        program.setListFieldMapping(listFieldMapping);
    }
    
    public List<SubscriptionListField> getListFields() {
        return program.getFieldList(); //Assuming that FormListFieldSet has already loaded it
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }
    
    public void startImport() {
        InputStream is = null;
        try {
            Map<Integer,String> mapping = getListFieldMapping();
            is = getFile().getInputStream();
        } catch (IOException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } finally {
            try {
                if(is != null) is.close();
            } catch (IOException ex) {
                FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
            }
        }
    }
    
    public void initMapping() {
        program.setListFieldMapping(new HashMap<Integer,String>());
    }
}
