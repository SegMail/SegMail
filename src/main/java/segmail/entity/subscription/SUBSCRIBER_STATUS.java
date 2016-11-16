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
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BOUNCED("BOUNCED");
    
    final String name;
    
    private SUBSCRIBER_STATUS(String name){
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
}
