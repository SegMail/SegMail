/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import seca2.bootstrap.UserRequestContainer;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public abstract class Program implements Serializable {
    
    @Inject
    protected UserRequestContainer reqContainer;
    
    @PostConstruct
    public void init() {
        initProgramParams();
    }
    
    /**
     * 
     */
    public abstract void initProgramParams();
}
