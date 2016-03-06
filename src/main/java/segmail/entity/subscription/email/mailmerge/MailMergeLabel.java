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

public enum MailMergeLabel {
    CONFIRM("CONFIRM","[!confirm]"),
    UNSUBSCRIBE("UNSUBSCRIBE","[!unsubscribe]");
    
    final String name;
    final String label;
    
    private MailMergeLabel(String name, String label){
        this.name = name;
        this.label = label;
    }
    
    public String label() {
        return this.label;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
}
