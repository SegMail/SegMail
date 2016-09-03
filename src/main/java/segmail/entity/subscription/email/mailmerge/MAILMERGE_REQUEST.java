/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email.mailmerge;

/**
 *
 * @author LeeKiatHaw
 */

public enum MAILMERGE_REQUEST {
    CONFIRM("CONFIRM","!confirm",86400000),
    UNSUBSCRIBE("UNSUBSCRIBE","!unsubscribe",-1);
    
    final String name;
    final String label;
    final int expiry; //-1 for no expiry
    
    private MAILMERGE_REQUEST(String name, String label, int expiry){
        this.name = name;
        this.label = label;
        this.expiry = expiry;
    }
    
    public String label() {
        return this.label;
    }
    
    public int expiry() {
        return this.expiry;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
    
    public String toCapFirstLetter() {
        String firstLetter = this.name.substring(0, 1);
        String theRest = this.name.toLowerCase().substring(1);
        theRest = firstLetter + theRest;
        return theRest;
    }
    
    public static MAILMERGE_REQUEST getByLabel(String label) {
        if(CONFIRM.label().equals(label))
            return CONFIRM;
        if(UNSUBSCRIBE.label().equals(label))
            return UNSUBSCRIBE;
        
        return null; //Default
    }
}
