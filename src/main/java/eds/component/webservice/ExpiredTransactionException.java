/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.webservice;

/**
 *
 * @author LeeKiatHaw
 */
public class ExpiredTransactionException extends Exception{

    public ExpiredTransactionException() {
    }

    public ExpiredTransactionException(String message) {
        super(message);
    }
    
}
