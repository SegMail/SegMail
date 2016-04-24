/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.data.IncompleteDataException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Singleton
public class CronService {

    @EJB
    BatchProcessingService batchService;

    //@Schedule(second="*/10", minute = "*", hour = "*")
    public void cron() {
        try {
            System.out.println("Thread " + Thread.currentThread().getId() + " running."); //debug
            DateTime now = new DateTime();
            java.sql.Timestamp nowTS = new java.sql.Timestamp(now.getMillis());
            batchService.processBatchJobQueue(nowTS);
        } catch (IncompleteDataException ex) {
            Logger.getLogger(CronService.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.out);
        }
    }
}
