/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.signup;

import eds.component.data.IncompleteDataException;
import eds.component.user.UserService;
import eds.entity.user.UserAccount;
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
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

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
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadOwnLists();
            selectDefaultList();
            getListFieldsJson();
        }
    }
    
    public void loadOwnLists() {
        List<SubscriptionList> ownedList = listService.getAllListForClient(clientCont.getClient().getOBJECTID());
        setOwnedLists(ownedList);
    }
    
    /**
     * Select the first list as default
     */
    public void selectDefaultList() {
        if(getOwnedLists()== null || getOwnedLists().isEmpty())
            return;
        
        SubscriptionList firstList = getOwnedLists().get(0);
        this.setSelectedListId(firstList.getOBJECTID());
        
    }
    
    public String getListFieldsJson() {
        String signupCode = generateListFields();
        program.setListFieldsJson(signupCode);
        
        return signupCode;
    }
    
    /*public String generateListFields() throws IncompleteDataException {

        List<String> selectedLists = getSelectedLists();
        
        List<SubscriptionListField> fieldList = getFieldList();
        
        landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
        
        String signupCode = "<form action=\"http://localhost:8080/SegMail/rest/subscribe\" "
                + "method=\"POST\"\n" +
"              enctype=\"application/x-www-form-urlencoded\"\n" +
"              >\n" +
"            <input type=\"hidden\" name=\"list\" value=\"" + "" + " ></input>\n" +
"            <input type=\"hidden\" name=\"client\" value=\""+ clientCont.getClient().getOBJECTID() +"\"></input>\n" +
"            <label for=\"email\">\n" +
"                Email:\n" +
"            </label>\n" +
"                <input type=\"text\" name=\"email\" />\n" +
"            \n" +
"                <button>Subscribe</button>\n" +
"                <input type=\"submit\" value=\"Submit\">\n" +
"        </form>";
        
        return signupCode;
    }*/
    

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
            ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
            String url = server.getURI() ;
            if(!url.endsWith("/"))
                url += "/";
            
            UserAccount userAccount = userService.getUserAccountById(userSessCont.getUser().getOBJECTID());
            
            if(userAccount == null)
                throw new RuntimeException("You do not have a user account set up yet, please contact your administrators to help you with this.");
            
            if(userAccount.getAPI_KEY() == null || userAccount.getAPI_KEY().isEmpty())
                userAccount = userService.regenerateAPIKey(userAccount.getOWNER().getOBJECTID());
            
            url += "rest/subscribe/"+userAccount.getAPI_KEY();
            return url;
        } catch (IncompleteDataException ex) {
            Logger.getLogger(FormSignupCode.class.getName()).log(Level.SEVERE, null, ex);
            return "["+ex.getMessage()+"]";
        }
    }
}
