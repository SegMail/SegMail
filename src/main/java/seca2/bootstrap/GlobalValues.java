/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author vincent.a.lee
 */
//@ApplicationScoped
public class GlobalValues {
    
    private final int MAX_RESULT_SIZE_DB = 9999;
    private final String CONTEXT_PATH_TOKEN = "[contextpath]";
    private final String PROGRAM_CONTEXT_NAME = "program";
    private final String CONTEXT_PATH = "SegMail/";
    
    public static final String SESSION_EXPIRED_MESSAGE_NAME = "SESSION_EXPIRED_MESSAGE";
    public static final String SESSION_EXPIRED_MESSAGE = "Your session has expired. Please login again.";
    
    private boolean INSTALLED;
        
    @PostConstruct
    public void init(){
        
    }

    public boolean isINSTALLED() {
        return INSTALLED;
    }

    public void setINSTALLED(boolean INSTALLED) {
        this.INSTALLED = INSTALLED;
    }

    public int getMAX_RESULT_SIZE_DB() {
        return MAX_RESULT_SIZE_DB;
    }

    public String getCONTEXT_PATH_TOKEN() {
        return CONTEXT_PATH_TOKEN;
    }

    public String getPROGRAM_CONTEXT_NAME() {
        return PROGRAM_CONTEXT_NAME;
    }

    public String getCONTEXT_PATH() {
        return CONTEXT_PATH;
    }
    
    
}
