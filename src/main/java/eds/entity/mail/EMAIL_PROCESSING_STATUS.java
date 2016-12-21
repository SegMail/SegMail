/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

/**
 *
 * @author LeeKiatHaw
 */
public enum EMAIL_PROCESSING_STATUS {
    HOLD("HOLD"),
    QUEUED("QUEUED"),
    SENT("SENT"),
    ERROR("ERROR"),
    BOUNCED("BOUNCED");
    
    public final String label;
    
    private EMAIL_PROCESSING_STATUS(String label){
        this.label = label;
    }
    
}
