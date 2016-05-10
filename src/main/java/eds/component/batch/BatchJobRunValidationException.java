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
public class BatchJobRunValidationException extends Exception {

    public BatchJobRunValidationException() {
    }

    public BatchJobRunValidationException(String message) {
        super(message);
    }

    public BatchJobRunValidationException(Throwable cause) {
        super(cause);
    }
    
}
