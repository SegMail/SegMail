/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

/**
 *
 * @author LeeKiatHaw
 */
public class TXStatusListener {

    public void onInProgress(@Observes String msg) {
        System.out.println("In progress: " + msg);
    }

    public void onSuccess(@Observes(during = TransactionPhase.AFTER_SUCCESS) String msg) {
        System.out.println("After success: " + msg);
    }

    public void onFailure(@Observes(during = TransactionPhase.AFTER_FAILURE) String msg) {
        System.out.println("After failure: " + msg);
    }
}
