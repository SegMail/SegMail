/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.transaction.TransactionStatus;

/**
 *
 * @author LeeKiatHaw
 */
public enum EMAIL_PROCESSING_STATUS implements TransactionStatus {
    HOLD("HOLD","EMAIL_HOLD","Email"),
    QUEUED("QUEUED","EMAIL_QUEUED","QueuedEmail"),
    SENT("SENT","EMAIL_SENT","SentEmail"),
    ERROR("ERROR","EMAIL_ERROR","ErrorEmail"),
    BOUNCED("BOUNCED","EMAIL_BOUNCED","BouncedEmail"),
    
    /**
     * This is meant for the old EMAIL table as we are moving to count only 
     * the individual status tables.
     */
    LEGACY_SENT("SENT","EMAIL","Email");
    
    public final String label;
    
    public final String tableName;
    
    public final String className;
    
    private <E extends Email> EMAIL_PROCESSING_STATUS(String label, String tableName, String className){
        this.label = label;
        this.tableName = tableName;
        this.className = className;
    }

    @Override
    public String getStatus() {
        return this.label;
    }

    @Override
    public String tableName() {
        return this.tableName;
    }

    @Override
    public String className() {
        return this.className;
    }

    
}
