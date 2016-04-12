/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm.webservice;

/**
 * Don't mark this for rollback.
 * Used for tracking if users access unwanted pages
 * 
 * @author LeeKiatHaw
 */
public class UnwantedAccessException extends Exception {

    public UnwantedAccessException() {
    }

    public UnwantedAccessException(String message) {
        super(message);
    }
    
    
}
