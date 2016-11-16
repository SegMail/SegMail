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
    CONFIRM("CONFIRM","{!confirm}",86400000,"confirm","Confirm"),
    UNSUBSCRIBE("UNSUBSCRIBE","{!unsubscribe}",-1,"unsubscribe","Unsubscribe");
    
    public final String name;
    public final String label;
    public final int expiry; //-1 for no expiry
    public final String program;
    public final String defaultHtmlText;
    
    private MAILMERGE_REQUEST(String name, String label, int expiry, String program, String htmlText){
        this.name = name;
        this.label = label;
        this.expiry = expiry;
        this.program = program;
        this.defaultHtmlText = htmlText;
    }
    
    public String label() {
        return this.label;
    }
    
    public int expiry() {
        return this.expiry;
    }
    
    public String program() {
        return this.program;
    }
    
    public String defaultHtmlText() {
        return this.defaultHtmlText;
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
