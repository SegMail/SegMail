/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.program;

import eds.entity.data.EnterpriseRelationship;
import eds.entity.user.UserType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="PROGRAM_ASSIGNMENT")
public class ProgramAssignment extends EnterpriseRelationship<Program,UserType> {
    
    private boolean DEFAULT_ASSIGNMENT;

    public ProgramAssignment() {
    }

    public ProgramAssignment(Program s, UserType t) {
        super(s, t);
    }
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isDEFAULT_ASSIGNMENT() {
        return DEFAULT_ASSIGNMENT;
    }

    public void setDEFAULT_ASSIGNMENT(boolean DEFAULT_ASSIGNMENT) {
        this.DEFAULT_ASSIGNMENT = DEFAULT_ASSIGNMENT;
    }
    
    
}
