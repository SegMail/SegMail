package segmail.program.list;

import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriptionList;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Program;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Shared across the entire /list/ program page
 * @author LeeKiatHaw
 */
@Named("ProgramList")
@SessionScoped
public class ProgramList extends Program implements Serializable {
    
    private List<SubscriptionList> allLists;
    private SubscriptionList listEditing;
    
    //For controlling the tabs
    private Map<String,Boolean> showActiveTabs;
    
    //For subscribers
    private Map<Long,Map<String,String>> subscriberTable;
    private SubscriberAccount subscriber;
    private Map<String,Object> fieldValues; //For adding new subscribers
    private int page = 1; //For data-dripping purpose
    
    //For the confirmation emails
    private List<AutoresponderEmail> confirmationEmails;
    private AutoresponderEmail selectedConfirmationEmail;
    private long selectedConfirmationEmailId;
    //For the welcome emails
    private List<AutoresponderEmail> welcomeEmails;
    private AutoresponderEmail selectedWelcomeEmail;
    private long selectedWelcomeEmailId;
    
    //For field list
    private List<SubscriptionListField> fieldList;
    private SubscriptionListField newField;
    
    
    private final String formName = "ProgramList";
    
    private boolean startFirstList;
    
    public ProgramList(){
    }
    
    //@PostConstruct
    @Override
    public void init(){
        
    }

    public SubscriptionList getListEditing() {
        return listEditing;
    }

    public void setListEditing(SubscriptionList listEditing) {
        this.listEditing = listEditing;
    }    

    public boolean isStartFirstList() {
        return startFirstList;
    }

    public void setStartFirstList(boolean startFirstList) {
        this.startFirstList = startFirstList;
    }

    public List<SubscriptionList> getAllLists() {
        return allLists;
    }

    public void setAllLists(List<SubscriptionList> allLists) {
        this.allLists = allLists;
    }

    public List<AutoresponderEmail> getConfirmationEmails() {
        return confirmationEmails;
    }

    public void setConfirmationEmails(List<AutoresponderEmail> confirmationEmails) {
        this.confirmationEmails = confirmationEmails;
    }

    public AutoresponderEmail getSelectedConfirmationEmail() {
        return selectedConfirmationEmail;
    }

    public void setSelectedConfirmationEmail(AutoresponderEmail selectedConfirmationEmail) {
        this.selectedConfirmationEmail = selectedConfirmationEmail;
        this.setSelectedConfirmationEmailId(
                (selectedConfirmationEmail == null) ? -1 :
                        selectedConfirmationEmail.getOBJECTID());
    }

    public String getFormName() {
        return formName;
    }

    public long getSelectedConfirmationEmailId() {
        return selectedConfirmationEmailId;
    }

    public void setSelectedConfirmationEmailId(long selectedConfirmationEmailId) {
        this.selectedConfirmationEmailId = selectedConfirmationEmailId;
    }

    public List<AutoresponderEmail> getWelcomeEmails() {
        return welcomeEmails;
    }

    public void setWelcomeEmails(List<AutoresponderEmail> welcomeEmails) {
        this.welcomeEmails = welcomeEmails;
    }

    public AutoresponderEmail getSelectedWelcomeEmail() {
        return selectedWelcomeEmail;
    }

    public void setSelectedWelcomeEmail(AutoresponderEmail selectedWelcomeEmail) {
        this.selectedWelcomeEmail = selectedWelcomeEmail;
        this.setSelectedWelcomeEmailId(
                (selectedWelcomeEmail == null) ? -1 : 
                    selectedWelcomeEmail.getOBJECTID());
    }

    public long getSelectedWelcomeEmailId() {
        return selectedWelcomeEmailId;
    }

    public void setSelectedWelcomeEmailId(long selectedWelcomeEmailId) {
        this.selectedWelcomeEmailId = selectedWelcomeEmailId;
    }
    
    public void refresh(){
        try {
            //redirect to itself after setting list editing
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //Keep all messages posted in this request
            ec.getFlash().setKeepMessages(true);
            ec.redirect(ec.getRequestContextPath()+"/".concat(reqContainer.getProgramName()));
        } catch (Exception ex){
            FacesMessenger.setFacesMessage(this.getFormName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        }
    }

    public List<SubscriptionListField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<SubscriptionListField> fieldList) {
        this.fieldList = fieldList;
    }
    
    public long getListEditingId(){
        return (getListEditing() == null) ? -1 : getListEditing().getOBJECTID();
    }
    
    public FIELD_TYPE[] getFieldTypes(){
        return FIELD_TYPE.values();
    }

    public SubscriptionListField getNewField() {
        return newField;
    }

    public void setNewField(SubscriptionListField newField) {
        this.newField = newField;
    }

    public SubscriberAccount getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(SubscriberAccount subscriber) {
        this.subscriber = subscriber;
    }

    public Map<String, Object> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, Object> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Map<Long, Map<String, String>> getSubscriberTable() {
        return subscriberTable;
    }

    public void setSubscriberTable(Map<Long, Map<String, String>> subscriberTable) {
        this.subscriberTable = subscriberTable;
    }

    public Map<String, Boolean> getShowActiveTabs() {
        return showActiveTabs;
    }

    public void setShowActiveTabs(Map<String, Boolean> showActiveTabs) {
        this.showActiveTabs = showActiveTabs;
    }

    @Override
    public void initProgramParams() {
        
    }

    @Override
    public void initProgram() {
        
    }

    
}
