/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource.synchronize;

import java.util.List;

/**
 * Synchronized list between the source and the target. It tells you what is to 
 * be added or removed from both lists.
 * 
 * - If an object is found in source, it might or might not be added to target.
 * - If an object is found in target, it might or might not be added to source. 
 * 
 * The Lifecycle of the SyncList object should be:
 * 1) Add all found in source and target into SyncList by calling foundInSource(Object) 
 * and foundInTarget(Object).
 * 2) Get the list of objects to be added or removed from both lists by calling
 * getAddTo[Source/Target] getRemoveFrom[Source/Target]
 * 
 * 
 * @author LeeKiatHaw
 */
public class SyncList {
    
    
    public void foundInSource(Object o) {
        
    }
    
    public void foundInTarget(Object o) {
        
    }
    
    public List<Object> getAddToSource() {
        return null;
        
    }
    
    public List<Object> getRemoveFromSource() {
        return null;
        
    }
    
    public List<Object> getAddToTarget() {
        return null;
        
    }
    
    public List<Object> getRemoveFromTarget() {
        return null;
        
    }
    
    public List<Object> purgeAddToSource() {
        return null;
        
    }
    
    public List<Object> purgeRemoveFromSource() {
        return null;
        
    }
    
    public List<Object> purgeAddToTarget() {
        return null;
        
    }
    
    public List<Object> purgeRemoveFromTarget() {
        return null;
        
    }
}
