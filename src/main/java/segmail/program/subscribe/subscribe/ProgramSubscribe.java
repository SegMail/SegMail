/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe;

import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import seca2.program.Program;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ProgramSubscribe")
public class ProgramSubscribe extends Program {
    
    private long clientId;
    private long listId;
    private Map<String,String> subscriberFieldValues;
    private boolean error;
    private String errorMessage;
    private String listname = "";
    private String confirmationKey;
    
    private String pageName;
    private final String PAGE_SUCCESS = "PAGE_SUCCESS";
    private final String PAGE_ALREADY_SUBSCRIBED_AND_RESEND = "PAGE_ALREADY_SUBSCRIBED_AND_RESEND";
    private final String PAGE_GENERIC_ERROR = "PAGE_GENERIC_ERROR";
    
    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public Map<String, String> getSubscriberFieldValues() {
        return subscriberFieldValues;
    }

    public void setSubscriberFieldValues(Map<String, String> subscriberFieldValues) {
        this.subscriberFieldValues = subscriberFieldValues;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPAGE_SUCCESS() {
        return PAGE_SUCCESS;
    }

    public String getPAGE_ALREADY_SUBSCRIBED_AND_RESEND() {
        return PAGE_ALREADY_SUBSCRIBED_AND_RESEND;
    }

    public String getPAGE_GENERIC_ERROR() {
        return PAGE_GENERIC_ERROR;
    }

    public String getConfirmationKey() {
        return confirmationKey;
    }

    public void setConfirmationKey(String confirmationKey) {
        this.confirmationKey = confirmationKey;
    }
    
    @Override
    public void clearVariables() {
        listId = 0;
        clientId = 0;
        subscriberFieldValues = new HashMap<>();
        pageName = "";
        confirmationKey = "";
    }

    @Override
    public void initRequestParams() {
        //put this at the endpoint!
        Map<String,String[]> paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        for(String key : paramMap.keySet()) {
            String[] params = paramMap.get(key);
            if(params.length <= 0 || params[0] == null || params[0].isEmpty())
                continue;
            
            if("client".equalsIgnoreCase(key))  {
                
                setClientId(Long.parseLong(params[0]));
                continue;
            }
            if("list".equalsIgnoreCase(key)) {
                setListId(Long.parseLong(params[0]));
                continue;
            }
            subscriberFieldValues.put(key, params[0]);
        }
        
    }

    @Override
    public void initProgram() {
        
    }
    
}
