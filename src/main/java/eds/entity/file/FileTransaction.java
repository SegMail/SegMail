/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.file;

import eds.entity.transaction.EnterpriseTransaction;
import eds.entity.transaction.TransactionStatus;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="FILE_TRANSACTION")
public class FileTransaction extends EnterpriseTransaction {
    
    private String NAME;
    private String CHECKSUM;
    private int LAST_PROCESSING_POSITION;
    
    private String LOCATION;

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getCHECKSUM() {
        return CHECKSUM;
    }

    public void setCHECKSUM(String CHECKSUM) {
        this.CHECKSUM = CHECKSUM;
    }

    public int getLAST_PROCESSING_POSITION() {
        return LAST_PROCESSING_POSITION;
    }

    public void setLAST_PROCESSING_POSITION(int LAST_PROCESSING_POSITION) {
        this.LAST_PROCESSING_POSITION = LAST_PROCESSING_POSITION;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String LOCATION) {
        this.LOCATION = LOCATION;
    }

    @Override
    public <Ts extends TransactionStatus> Ts PROCESSING_STATUS() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FileTransaction transit(TransactionStatus newStatus, DateTime dt) {
        return this;
    }
    
    
    
}
