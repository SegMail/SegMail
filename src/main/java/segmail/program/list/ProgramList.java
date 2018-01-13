package segmail.program.list;

import eds.entity.client.VerifiedSendingAddress;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriptionList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import seca2.program.Program;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.entity.subscription.datasource.ListDataMapping;
import segmail.entity.subscription.datasource.ListDatasource;

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
    private String activeTab;
    final String[] TABS = {
        "settings", // Default is the 1st 
        "fieldset",
        "redirect",
        "datasource"
    };
    
    //For subscribers
    private Map<Long,Map<String,String>> subscriberTable;
    private SubscriberAccount subscriber;
    private Map<String,Object> fieldValues; //For adding new subscribers
    private int page = 0; //For data-dripping purpose
    
    //For the confirmation emails
    private List<AutoresponderEmail> confirmationEmails;
    private AutoresponderEmail selectedConfirmationEmail;
    private long selectedConfirmationEmailId;
    //For the welcome emails
    private List<AutoresponderEmail> welcomeEmails;
    private AutoresponderEmail selectedWelcomeEmail;
    private long selectedWelcomeEmailId;
    
    private List<String> confirmUrlParams;
    private List<String> welcomeUrlParams;
    private List<String> unsubUrlParams;
    
    //For field list
    private List<SubscriptionListField> fieldList;
    private SubscriptionListField newField;
    
    //For import
    private Map<Integer,String> listFieldMapping = new HashMap<>();
    
    //For Verified Addresses
    private List<VerifiedSendingAddress> verifiedAddresses;
    private String sendingAddress;
    
    //For Datasources
    private ListDatasource newDatasource;
    private List<ListDataMapping> datasourceMappings;
    private String oldPassword;
    private List<String> remoteDBFields;
    private String connectionString; //For the purpose of caching remoteDBFields and not querying the remote DB all the time
    private boolean useStatusField;
    private List<String> statusFields;
    private String statusField;
    private List<String> statusFieldValues;
    private String statusFieldValue;
    private String oldStatusField;
    private String oldStatusFieldValue;
    
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

    public Map<Integer, String> getListFieldMapping() {
        return listFieldMapping;
    }

    public void setListFieldMapping(Map<Integer, String> listFieldMapping) {
        this.listFieldMapping = listFieldMapping;
    }

    public List<VerifiedSendingAddress> getVerifiedAddresses() {
        return verifiedAddresses;
    }

    public void setVerifiedAddresses(List<VerifiedSendingAddress> verifiedAddresses) {
        this.verifiedAddresses = verifiedAddresses;
    }

    public String getSendingAddress() {
        return sendingAddress;
    }

    public void setSendingAddress(String sendingAddress) {
        this.sendingAddress = sendingAddress;
    }

    public ListDatasource getNewDatasource() {
        return newDatasource;
    }

    public void setNewDatasource(ListDatasource newDatasource) {
        this.newDatasource = newDatasource;
    }

    public List<ListDataMapping> getDatasourceMappings() {
        return datasourceMappings;
    }

    public void setDatasourceMappings(List<ListDataMapping> datasourceMappings) {
        this.datasourceMappings = datasourceMappings;
    }

    public List<String> getRemoteDBFields() {
        return remoteDBFields;
    }

    public void setRemoteDBFields(List<String> remoteDBFields) {
        this.remoteDBFields = remoteDBFields;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public boolean isUseStatusField() {
        return useStatusField;
    }

    public void setUseStatusField(boolean useStatusField) {
        this.useStatusField = useStatusField;
    }

    public String getStatusField() {
        return statusField;
    }

    public void setStatusField(String statusField) {
        this.statusField = statusField;
    }

    public List<String> getStatusFieldValues() {
        return statusFieldValues;
    }

    public void setStatusFieldValues(List<String> statusFieldValues) {
        this.statusFieldValues = statusFieldValues;
    }

    public String getStatusFieldValue() {
        return statusFieldValue;
    }

    public void setStatusFieldValue(String statusFieldValue) {
        this.statusFieldValue = statusFieldValue;
    }

    public List<String> getStatusFields() {
        return statusFields;
    }

    public void setStatusFields(List<String> statusFields) {
        this.statusFields = statusFields;
    }

    public String getOldStatusField() {
        return oldStatusField;
    }

    public void setOldStatusField(String oldStatusField) {
        this.oldStatusField = oldStatusField;
    }

    public String getOldStatusFieldValue() {
        return oldStatusFieldValue;
    }

    public void setOldStatusFieldValue(String oldStatusFieldValue) {
        this.oldStatusFieldValue = oldStatusFieldValue;
    }

    public List<String> getConfirmUrlParams() {
        return confirmUrlParams;
    }

    public void setConfirmUrlParams(List<String> confirmUrlParams) {
        this.confirmUrlParams = confirmUrlParams;
    }

    public List<String> getWelcomeUrlParams() {
        return welcomeUrlParams;
    }

    public void setWelcomeUrlParams(List<String> welcomeUrlParams) {
        this.welcomeUrlParams = welcomeUrlParams;
    }

    public List<String> getUnsubUrlParams() {
        return unsubUrlParams;
    }

    public void setUnsubUrlParams(List<String> unsubUrlParams) {
        this.unsubUrlParams = unsubUrlParams;
    }

    public String getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(String activeTab) {
        this.activeTab = activeTab;
    }
    
    @Override
    public void initRequestParams() {
        
    }

    @Override
    public void initProgram() {
        
    }

    @Override
    public void clearVariables() {
        
    }

    
}
