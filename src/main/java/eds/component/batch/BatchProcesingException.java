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
public class BatchProcesingException extends Exception {

    public BatchProcesingException() {
    }

    public BatchProcesingException(String message) {
        super(message);
    }

    public BatchProcesingException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
