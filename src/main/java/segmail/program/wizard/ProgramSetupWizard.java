/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import eds.entity.client.VerifiedSendingAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import seca2.program.Program;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
@Named("ProgramSetupWizard")
public class ProgramSetupWizard extends Program {
    
    private final String TAB_WELCOME = "welcome";
    private final String TAB_ADDRESS = "address";
    private final String TAB_LIST = "list";
    private final String TAB_AUTORESPONDERS = "auto";
    private final String TAB_COLLECT = "collect";
    private final String TAB_SEND = "send";
    
    private final String KEY_NAME = "name";
    private final String KEY_VIEW_LOCATION = "location";
    private final String KEY_COMPLETED = "completed";
    
    private List<String> stages;
    private Map<Integer,Map<String,Object>> stagesMap;
    private String lastStage;
    private int currentStage;
    
    // Address tab
    private String address;
    private List<VerifiedSendingAddress> existingAddresses;
    private VerifiedSendingAddress selectedAddress;
    
    // List tab
    private String listname;
    public String[] getDefaultListFields() {
        return ListService.DEFAULT_FIELD_NAMES;
    }
    private List<SubscriptionList> existingLists;
    private SubscriptionList selectedList;
    
    // Autoresponder tab
    private List<SubscriptionListField> listFields;
    private final MAILMERGE_REQUEST[] mailmergeLinkTags = MAILMERGE_REQUEST.values();
    private Map<String,String> mailmergeLinks = new HashMap<>();
    private List<String> listTags;
    private AutoresponderEmail confirmEmail;
    private AutoresponderEmail welcomeEmail;

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<VerifiedSendingAddress> getExistingAddresses() {
        return existingAddresses;
    }

    public void setExistingAddresses(List<VerifiedSendingAddress> existingAddresses) {
        this.existingAddresses = existingAddresses;
    }

    public List<SubscriptionList> getExistingLists() {
        return existingLists;
    }

    public void setExistingLists(List<SubscriptionList> existingLists) {
        this.existingLists = existingLists;
    }
    
    public String getKEY_NAME() {
        return KEY_NAME;
    }

    public String getKEY_VIEW_LOCATION() {
        return KEY_VIEW_LOCATION;
    }

    public String getKEY_COMPLETED() {
        return KEY_COMPLETED;
    }

    public String getTAB_ADDRESS() {
        return TAB_ADDRESS;
    }

    public String getTAB_LIST() {
        return TAB_LIST;
    }

    public String getTAB_AUTORESPONDERS() {
        return TAB_AUTORESPONDERS;
    }

    public String getTAB_COLLECT() {
        return TAB_COLLECT;
    }

    public String getTAB_SEND() {
        return TAB_SEND;
    }
    
    public List<String> getStages() {
        return stages;
    }

    public void setStages(List<String> stages) {
        this.stages = stages;
    }

    public Map<Integer, Map<String, Object>> getStagesMap() {
        return stagesMap;
    }

    public void setStagesMap(Map<Integer, Map<String, Object>> stagesMap) {
        this.stagesMap = stagesMap;
    }

    public String getLastStage() {
        return lastStage;
    }

    public void setLastStage(String lastStage) {
        this.lastStage = lastStage;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public List<SubscriptionListField> getListFields() {
        return listFields;
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        this.listFields = listFields;
    }

    public Map<String, String> getMailmergeLinks() {
        return mailmergeLinks;
    }

    public void setMailmergeLinks(Map<String, String> mailmergeLinks) {
        this.mailmergeLinks = mailmergeLinks;
    }

    public List<String> getListTags() {
        return listTags;
    }

    public void setListTags(List<String> listTags) {
        this.listTags = listTags;
    }

    public String getTAB_WELCOME() {
        return TAB_WELCOME;
    }

    public VerifiedSendingAddress getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(VerifiedSendingAddress selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public SubscriptionList getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(SubscriptionList selectedList) {
        this.selectedList = selectedList;
    }

    public MAILMERGE_REQUEST[] getMailmergeLinkTags() {
        return mailmergeLinkTags;
    }

    public AutoresponderEmail getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(AutoresponderEmail confirmEmail) {
        this.confirmEmail = confirmEmail;
    }

    public AutoresponderEmail getWelcomeEmail() {
        return welcomeEmail;
    }

    public void setWelcomeEmail(AutoresponderEmail welcomeEmail) {
        this.welcomeEmail = welcomeEmail;
    }
    
    public Object fetchMapValue(String name, String key) {
        for(int i = 0; i < stages.size(); i++) {
            if (stages.get(i).equalsIgnoreCase(name)) {
                return stagesMap.get(i).get(key);
            }
        }
        return null;
    }
    
    public void updateMapValue(String name, String key, Object value) {
        for(int i = 0; i < stages.size(); i++) {
            if (stages.get(i).equalsIgnoreCase(name)) {
                stagesMap.get(i).put(key, value);
                return;
            }
        }
    }
    
    public void initStages() {
        stages = new ArrayList<>();
        stages.add(TAB_WELCOME);
        stages.add(TAB_ADDRESS);
        stages.add(TAB_LIST);
        stages.add(TAB_AUTORESPONDERS);
        stages.add(TAB_COLLECT);
        stages.add(TAB_SEND);
        
        stagesMap = new HashMap<>();
        for(int i = 0; i < stages.size(); i++) {
            stagesMap.put(i, new HashMap<>());
            stagesMap.get(i).put(KEY_NAME, stages.get(i));
            //stagesMap.get(i).put(KEY_VIEW_LOCATION, "/"+stages.get(i)+"/layout.xhtml");
            stagesMap.get(i).put(KEY_COMPLETED, false);
        }
        
    }
    
    public void initAddress() {
        this.address = "";
        this.existingAddresses = new ArrayList<>();
    }
    
    @Override
    public void clearVariables() {
        initStages();
        initAddress();
    }

    @Override
    public void initRequestParams() {
        
    }

    @Override
    public void initProgram() {
        
    }
    
    public boolean checkAddressDone() {
        return (boolean) this.fetchMapValue(this.getTAB_ADDRESS(), this.getKEY_COMPLETED());
    }
    public boolean checkListDone() {
        return (boolean) this.fetchMapValue(this.getTAB_LIST(), this.getKEY_COMPLETED());
    }
    public boolean checkAutoDone() {
        return (boolean) this.fetchMapValue(this.getTAB_AUTORESPONDERS(), this.getKEY_COMPLETED());
    }
    public boolean checkCollectDone() {
        return (boolean) this.fetchMapValue(this.getTAB_COLLECT(), this.getKEY_COMPLETED());
    }
    public boolean checkSendDone() {
        return (boolean) this.fetchMapValue(this.getTAB_SEND(), this.getKEY_COMPLETED());
    }
}
