/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

/**
 *
 * @author LeeKiatHaw
 */
public enum LAST_SYNC_RESULT {
    
    COMPLETE("COMPLETE"),
    INCOMPLETE("INCOMPLETE"),
    CONN_ERROR("CONN_ERROR"),
    NO_MAPPING("NO_MAPPING"),
    NO_CLIENT("NO_CLIENT");
    
    public final String label;
    
    private LAST_SYNC_RESULT(String label){
        this.label = label;
    }
}
