/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormNavigation")
public class FormNavigation {
    
    @Inject
    UserRequestContainer reqCont;
    @Inject
    UserSessionContainer sessCont;
    
    /**
     * Gets the "level" of the program based on the request URL in UserRequestContainer.
     * Eg. "/campaign" is 0, "/campaign/32/112" is 2.
     * 
     * @return 
     */
    public int programLevel() {
        return reqCont.getProgramParamsOrdered().size();
    }
}
