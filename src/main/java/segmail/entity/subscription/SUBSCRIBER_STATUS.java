/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

/**
 *
 * @author LeeKiatHaw
 */
public enum SUBSCRIBER_STATUS {
    NEW("NEW"),
    VERIFIED("VERIFIED"), // Verified email
    REMOVED("REMOVED"), // Removed by administrator, might be restored later
    UNSUBSRIBE_ALL("UNSUBSRIBE_ALL"), // Remove themselves voluntarily, once they have removed themselves from all lists, then they will be in UNSUBSCRIBE_ALL status
    BOUNCED("BOUNCED"); // Any hard bounces
    
    public final String name;
    
    private SUBSCRIBER_STATUS(String name){
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
}
