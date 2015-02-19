/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author vincent.a.lee
 */
@ApplicationScoped
public class GlobalValues {
    
    private final int MAX_RESULT_SIZE_DB = 9999;
    
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
    
    
}
