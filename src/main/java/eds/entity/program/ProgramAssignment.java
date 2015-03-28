/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.program;

import eds.entity.EnterpriseRelationship;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="PROGRAM_ASSIGNMENT")
public class ProgramAssignment extends EnterpriseRelationship {

    //Any additional attributes to be maintained for this relationship?
    //protected String REL_TYPE = "MENU_ITEM_ACCESS"; no need to redefine it here
    
    /*@PrePersist
    public void prePersist(){
        this.REL_TYPE = "PROGRAM_ASSIGNMENT";
    }
    
    @PreUpdate
    public void preUpdate(){
        this.REL_TYPE = "PROGRAM_ASSIGNMENT";
    }*/
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
