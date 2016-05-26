/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import javax.ejb.ApplicationException;

/**
 *
 * @author LeeKiatHaw
 */
@ApplicationException(rollback = true)
public class BatchProcessingException extends Exception {

    public BatchProcessingException() {
    }

    public BatchProcessingException(String message) {
        super(message);
    }

    public BatchProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
