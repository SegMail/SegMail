/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import eds.component.client.ClientFacade;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.user.UserService;
import java.util.List;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
import static segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE.CONFIRMATION;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormAddNewAutoEmail")
@RequestScoped
public class FormAddNewAutoEmail {
    
    @EJB private AutoresponderService autoresponderService;
    @EJB private UserService userService;
    @EJB private ListService listService;
    
    @Inject private ProgramAutoresponder program;
    @Inject private ClientFacade clientFacade;
    @Inject private UserRequestContainer reqContainer;
    
    private String subject;
    
    private final String defaultConfirmBody = 
            "<p><span>Dear <strong>[insert mailmerge tag using the \"Mailmerge Tag\" button on top]</strong></span>,</p>"
            +"<p><span></span></p>"
            + "<p><span>Please click on "+MAILMERGE_REQUEST.CONFIRM.label()+" to confirm your subscription.</span></p>";
    
    //private AutoEmailTypeFactory.TYPE type;
    private AUTO_EMAIL_TYPE type;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadLists();
        }
    }
    
    public void addNewAutoEmail(){
        try {
            if(getSelectedListId() <= 0)
                throw new IncompleteDataException("Please select a list.");
            // Create the new template
            // Get the client associated with the user and assign it
            AutoresponderEmail newTemplate = autoresponderService.createAndAssignAutoEmail(
                    subject, 
                    (type == CONFIRMATION) ? defaultConfirmBody : "", 
                    type);
            //Assign it to the selected list
            autoresponderService.assignAutoEmailToList(newTemplate.getOBJECTID(), getSelectedListId());
            
            program.refresh();
            
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (RelationshipExistsException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "There is already an email created and assigned to your account.", 
                    "Please click the refresh button on the top right hand corner to see if it's already there. "
                            + "Autoresponder emails are distinguished by their type and subject title."
                            + "If this problem persist, please contact your administrator. ");
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Oops..but this shouldn't happen.", 
                    "Please raise an issue to our administrator, we will try to resolve it shortly!");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, 
                    ex.getClass().getSimpleName()+": "+ex.getMessage(), "Please raise an issue to our administrator, we will try to resolve it shortly!");
        }
    }
    
    public void loadLists() {
        List<SubscriptionList> lists = listService.getAllListForClient(clientFacade.getClient().getOBJECTID());
        setLists(lists);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDefaultConfirmBody() {
        return defaultConfirmBody;
    }

    /*
    public AutoEmailTypeFactory.TYPE getType() {
        return type;
    }

    public void setType(AutoEmailTypeFactory.TYPE type) {
        this.type = type;
    }*/

    public AUTO_EMAIL_TYPE getType() {
        return type;
    }

    public void setType(AUTO_EMAIL_TYPE type) {
        this.type = type;
    }
    
    public List<SubscriptionList> getLists() {
        return program.getLists();
    }

    public void setLists(List<SubscriptionList> lists) {
        program.setLists(lists);
    }
    
    public long getSelectedListId() {
        return program.getSelectedListId();
    }

    public void setSelectedListId(long selectedListId) {
        program.setSelectedListId(selectedListId);
    }
    
}
