/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import javax.ejb.ApplicationException;

/**
 *
 * @author LeeKiatHaw
 */
@ApplicationException(rollback = true)
public class InvalidEmailException extends Exception {

    public InvalidEmailException(String message) {
        super(message);
    }
    
}
