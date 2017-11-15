/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.user;

/**
 * For password reset use only, because we don't want to let anyone check
 * if a particular exists so we just quietly tell them their request has been
 * successfully submitted. 
 * 
 * @author LeeKiatHaw
 */
public class UserNotFoundResetException extends Exception {

    public UserNotFoundResetException() {
    }

    public UserNotFoundResetException(String message) {
        super(message);
    }
    
}
