/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource.synchronize;

/**
 *
 * @author LeeKiatHaw
 */
public abstract class SyncListObject {
    
    /**
     * A comparable String that identifies this object.
     * 
     * @return 
     */
    public abstract String getId();

    /**
     * 
     * @param anotherObj
     * @return 
     */
    public boolean equals(SyncListObject anotherObj) {
        
        if(this.getId() == null || this.getId().isEmpty())
            return false;
        
        return this.getId().equals(anotherObj.getId());
    }
    
}
