/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email.mailmerge;

import eds.entity.transaction.TransactionStatus;

/**
 *
 * @author LeeKiatHaw
 */
public enum MAILMERGE_STATUS implements TransactionStatus{
    UNPROCESSED("UNPROCESSED","MailMergeRequest","MAILMERGE_REQUEST"),
    PROCESSED("PROCESSED","MailMergeRequest","MAILMERGE_REQUEST");
    
    public final String name;
    
    public final String className;
    
    public final String tableName;
    
    private MAILMERGE_STATUS(String name, String className, String tableName){
        this.name = name;
        this.className = className;
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStatus() {
        return this.name;
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
