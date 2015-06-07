/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.subscription;

import javax.ejb.EJBException;

/**
 *
 * @author LeeKiatHaw
 */
public class ListException extends EJBException {

    private final String message;
    
    public ListException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
    
    
}
