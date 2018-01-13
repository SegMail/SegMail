/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.signup;

import eds.component.data.IncompleteDataException;
import eds.component.user.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import seca2.bootstrap.UserSessionContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSignupCode")
public class FormSignupCode {
    
    @Inject ProgramSignupCode program;
    @Inject ClientContainer clientCont;
    @Inject UserSessionContainer userSessCont;
    
    @EJB ListService listService;
    @EJB LandingService landingService;
    @EJB SubscriptionService subService;
    @EJB UserService userService;
    @EJB AutoresponderService autoemailService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadOwnLists();
            selectDefaultList();
            //getListFieldsJson();
            reloadList();
        }
        
    }
    
    public void reloadList() {
        loadSelectedList();
        loadConfirmationEmails();
        loadListFields();
    }
    
    public void loadOwnLists() {
        List<SubscriptionList> ownedList = listService.getAllListForClient(clientCont.getClient().getOBJECTID());
        setOwnedLists(ownedList);
    }
    
    /**
     * Select the first list as default
     */
    public void selectDefaultList() {
        setSelectedListId(-1);
        if(getOwnedLists()== null || getOwnedLists().isEmpty())
            return;
        
        SubscriptionList firstList = getOwnedLists().get(0);
        setSelectedListId(firstList.getOBJECTID());
        
    }
    
    public String getListFieldsJson() {
        String signupCode = generateListFields();
        program.setListFieldsJson(signupCode);
        
        return signupCode;
    }

    public void setListFieldsJson(String signupCode) {
        program.setListFieldsJson(signupCode);
    }
    
    public List<SubscriptionList> getOwnedLists() {
        return program.getOwnedLists();
    }

    public void setOwnedLists(List<SubscriptionList> ownedList) {
        program.setOwnedLists(ownedList);
    }
    
    public long getSelectedListId() {
        return program.getSelectedListId();
    }

    public void setSelectedListId(long selectedListId) {
        program.setSelectedListId(selectedListId);
    }
    
    public long getClientId() {
        return clientCont.getClient().getOBJECTID();
    }
    
    public List<SubscriptionListField> getFields() {
        return program.getFields();
    }

    public void setFields(List<SubscriptionListField> fields) {
        program.setFields(fields);
    }
    
    public List<AutoresponderEmail> getConfirmEmails() {
        return program.getConfirmEmails();
    }

    public void setConfirmEmails(List<AutoresponderEmail> confirmEmails) {
        program.setConfirmEmails(confirmEmails);
    }
    
    public SubscriptionList getSelectedList() {
        return program.getSelectedList();
    }

    public void setSelectedList(SubscriptionList selectedList) {
        program.setSelectedList(selectedList);
    }
    
    public void loadSelectedList() {
        long selectedListId = this.getSelectedListId();
        if(selectedListId <= 0)
            return;
        
        for(SubscriptionList list : getOwnedLists()) {
            if(list.getOBJECTID() == selectedListId){
                setSelectedList(list);
                return;
            }
        }
    }
    
    public void loadConfirmationEmails() {
        setConfirmEmails(new ArrayList<AutoresponderEmail>());
        if(this.getSelectedListId() <= 0)
            return;
            
        List<AutoresponderEmail> confirmEmails = autoemailService.getAssignedAutoEmailsForList(this.getSelectedListId(), AUTO_EMAIL_TYPE.CONFIRMATION);
        setConfirmEmails(confirmEmails);
    }
    
    public void loadListFields() {
        long selectedListId = this.getSelectedListId();
        if(selectedListId <= 0)
            return;
        
        //Get all the lists fields
        List<SubscriptionListField> fieldLists = listService.getFieldsForSubscriptionList(selectedListId);
        
        setFields(fieldLists);
    }
    
    /**
     * Deprecated
     * @return 
     */
    public String generateListFields() {
        long selectedListId = this.getSelectedListId();
        if(selectedListId <= 0)
            return "";
        
        //Get all the lists fields
        List<SubscriptionListField> fieldLists = listService.getFieldsForSubscriptionList(selectedListId);
        //Build a JSON object using the fieldList
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(SubscriptionListField field : fieldLists) {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            objBuilder.add("name", field.getFIELD_NAME());
            objBuilder.add("key", (String) field.generateKey());
            objBuilder.add("description", field.getDESCRIPTION());
            if(field.isMANDATORY())
                objBuilder.add("mandatory", true);
            arrayBuilder.add(objBuilder);
        }
        return arrayBuilder.build().toString();
    }
    
    public String getSignupLink() {
        try {
            ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.WEB);
            String url = server.getURI() ;
            if(!url.endsWith("/"))
                url += "/";
            
            url += "subscribe";//+userAccount.getAPI_KEY();
            return url;
        } catch (IncompleteDataException ex) {
            Logger.getLogger(FormSignupCode.class.getName()).log(Level.SEVERE, null, ex);
            return "["+ex.getMessage()+"]";
        }
    }
    
    
}
