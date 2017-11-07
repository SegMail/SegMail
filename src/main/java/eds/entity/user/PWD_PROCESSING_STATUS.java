/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.transaction.TransactionStatus;

/**
 *
 * @author LeeKiatHaw
 */
public enum PWD_PROCESSING_STATUS implements TransactionStatus {
    NEW("NEW","PasswordResetRequest","PASSWORD_RESET_REQUEST"),
    PROCESSED("PROCESSED","PasswordResetRequest","PASSWORD_RESET_REQUEST");
    
    public final String label;
    
    public final String className;
    
    public final String tableName;
    
    private PWD_PROCESSING_STATUS(String label, String className, String tableName){
        this.label = label;
        this.className = className;
        this.tableName = tableName;
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
