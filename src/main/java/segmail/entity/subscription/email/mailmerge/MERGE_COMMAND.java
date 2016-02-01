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
public enum MERGE_COMMAND {
    UNSUBSCRIBE("UNSUBSCRIBE"),
    CONFIRM("CONFIRM");
    
    final String name;
    
    private MERGE_COMMAND(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
