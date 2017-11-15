/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EMAIL_SENT")
public class SentEmail extends Email {
    
    public SentEmail() {
        this.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.SENT);
    }

    public SentEmail(Email email, DateTime sentTime) {
        super(email);
        
        this.setSCHEDULED_DATETIME(new Timestamp(sentTime.getMillis()));
        this.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.SENT);
    }
    
}
