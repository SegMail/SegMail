package segmail.program.reset;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eds.component.data.IncompleteDataException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import seca2.bootstrap.UserRequestContainer;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormResetSend")
public class FormResetSend {

    @Inject
    RestResetClient resetClient;
    @Inject
    ProgramReset program;
    @Inject
    UserRequestContainer reqContainer;
    @Inject
    LandingService landingService;

    //For deciding which form to show
    final String FORM_SEND = "SEND";
    final String FORM_SEND_SUCCESS = "SEND_SUCCESS";
    final String FORM_SEND_ERROR = "SEND_ERROR";
    final String FORM_NEW_PWD = "NEW_PWD";
    final String FORM_NEW_PWD_ERROR = "NEW_PWD_ERROR";
    final String FORM_NEW_PWD_PROCESSED = "NEW_PWD_PROCESSED";
    final String FORM_NEW_PWD_SUCCESS = "NEW_PWD_SUCCESS";

    private String email;
    private String password;

    private String loginURL;
    
    @PostConstruct
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            extractParams(reqContainer);
            initForm();
        }

    }

    public void sendResetEmail() {
        try {
            String token = resetClient.sendResetEmail(getEmail());
            setToken(token);
            setForm(FORM_SEND_SUCCESS);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            setForm(FORM_SEND_ERROR);
        }
    }
    
    public void retrieveRequest() {
        Response response = resetClient.retrieveRequestByToken(getToken());

        //Request was already processed
        if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            String error = response.readEntity(String.class);
            setForm(this.FORM_NEW_PWD_PROCESSED);
            return;
        }

        //Other errors
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            String error = response.readEntity(String.class);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, error, "");
            setForm(this.FORM_NEW_PWD);
            return;
        }
        
        //Success
        setForm(this.FORM_NEW_PWD);
    }
    
    public void resetPassword() {
        Response response = resetClient.resetPassword(getToken(),getPassword());
        
        //Request was already processed
        if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            String error = response.readEntity(String.class);
            setForm(this.FORM_NEW_PWD_PROCESSED);
            return;
        }

        //Other errors
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            String error = response.readEntity(String.class);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, error, "");
            setForm(this.FORM_NEW_PWD);
            return;
        }
        
        //Success
        setForm(this.FORM_NEW_PWD_SUCCESS);
        try {
            loginURL = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP).getURI();
        } catch (IncompleteDataException ex) {
            Logger.getLogger(FormResetSend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getForm() {
        return program.getForm();
    }

    public void setForm(String page) {
        program.setForm(page);
    }

    public String getFORM_SEND() {
        return FORM_SEND;
    }

    public String getFORM_SEND_ERROR() {
        return FORM_SEND_ERROR;
    }

    public String getFORM_NEW_PWD() {
        return FORM_NEW_PWD;
    }

    public String getFORM_NEW_PWD_ERROR() {
        return FORM_NEW_PWD_ERROR;
    }

    public String getFORM_SEND_SUCCESS() {
        return FORM_SEND_SUCCESS;
    }

    public String getFORM_NEW_PWD_PROCESSED() {
        return FORM_NEW_PWD_PROCESSED;
    }

    public String getFORM_NEW_PWD_SUCCESS() {
        return FORM_NEW_PWD_SUCCESS;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return program.getToken();
    }

    public void setToken(String token) {
        program.setToken(token);
    }

    public String getLoginURL() {
        return loginURL;
    }

    public void extractParams(UserRequestContainer reqContainer) {
        program.clearVariables();
        List<String> params = reqContainer.getProgramParamsOrdered();

        String token = (params != null && !params.isEmpty()) ? params.get(0) : "";
        setToken(token);
    }

    /**
     * Determines which form to show
     */
    public void initForm() {
        if (getToken() == null || getToken().isEmpty()) {
            setEmail("");
            setForm(this.FORM_SEND);
            return;
        }
        
        retrieveRequest();
        
    }
}
