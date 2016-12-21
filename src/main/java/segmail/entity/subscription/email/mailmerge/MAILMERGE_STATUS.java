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
public enum MAILMERGE_STATUS {
    UNPROCESSED("UNPROCESSED"),
    PROCESSED("PROCESSED");
    
    public final String name;
    
    private MAILMERGE_STATUS(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
}
