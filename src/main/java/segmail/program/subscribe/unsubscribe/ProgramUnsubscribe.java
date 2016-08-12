/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe;

import javax.inject.Named;
import seca2.program.Program;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ProgramUnsubscribe")
public class ProgramUnsubscribe extends Program {

    private final String SUCCESS = "SUCCESS";
    private final String LANDING = "LANDING";
    private final String PROCESSED = "PROCESSED";
    private final String EXPIRED = "EXPIRED";
    private final String ERROR = "ERROR";
    
    private String currentPage;
    
    private String listName;
    
    private String requestKey;
    
    @Override
    public void clearVariables() {
        this.setListName("");
        this.setRequestKey("");
        this.setCurrentPage("");
    }

    @Override
    public void initRequestParams() {
        
    }

    @Override
    public void initProgram() {
        
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getSUCCESS() {
        return SUCCESS;
    }

    public String getLANDING() {
        return LANDING;
    }

    public String getPROCESSED() {
        return PROCESSED;
    }

    public String getEXPIRED() {
        return EXPIRED;
    }

    public String getERROR() {
        return ERROR;
    }
    
    
}
