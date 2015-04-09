/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.layout;

import eds.entity.program.Program;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class LayoutListener {
    
    @PrePersist
    @PreUpdate
    public void PrePersistUpdate(Layout layout){
        
    }
}
