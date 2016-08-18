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
    
    private long userId;
    private long listId;
    private Map<String,String> subscriberFieldValues;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    @Override
    public void clearVariables() {
        listId = 0;
        userId = 0;
        subscriberFieldValues = new HashMap<>();
    }

    @Override
    public void initRequestParams() {
        //put this at the endpoint!
        Map<String,String[]> paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
        for(String key : paramMap.keySet()) {
            String[] params = paramMap.get(key);
            if(params.length <= 0 || params[0] == null || params[0].isEmpty())
                continue;
            
            if("userid".equalsIgnoreCase(key))  {
                
                setUserId(Long.parseLong(params[0]));
                continue;
            }
            if("listid".equalsIgnoreCase(key)) {
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
