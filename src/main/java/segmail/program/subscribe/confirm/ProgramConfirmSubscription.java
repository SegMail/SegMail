/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm;

import javax.inject.Named;
import seca2.program.Program;

/**
 * Offline program to process subscriptions at landing servers.
 * 
 * @author LeeKiatHaw
 */
@Named("ProgramConfirmSubscription")
public class ProgramConfirmSubscription extends Program {
    
    private String listName;
    
    private String requestKey;

    @Override
    public void initProgramParams() {
        
    }

    @Override
    public void initProgram() {
        
    }

    @Override
    public void clearVariables() {
        
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }
    
    
}
