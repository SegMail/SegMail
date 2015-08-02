/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphAPI;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import eds.entity.data.NodeType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EVALUATION_CHAIN_ELEMENT")
public class EvaluationChainElement implements Serializable {
    
    private Long ID;
    private boolean RECURSIVE;
    private final Class<? extends EnterpriseObject> e;
    private final Class<? extends EnterpriseRelationship> r;
    private final NodeType NODETYPE;

    public EvaluationChainElement(boolean RECURSIVE, Class<? extends EnterpriseObject> e, Class<? extends EnterpriseRelationship> r,NodeType type) {
        this.RECURSIVE = RECURSIVE;
        this.e = e;
        this.r = r;
        this.NODETYPE = type;
    }
    
    @Id
    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public boolean isRECURSIVE() {
        return RECURSIVE;
    }

    public void setRECURSIVE(boolean RECURSIVE) {
        this.RECURSIVE = RECURSIVE;
    }

    public Class<? extends EnterpriseObject> getE() {
        return e;
    }

    public Class<? extends EnterpriseRelationship> getR() {
        return r;
    }

    public NodeType getNODETYPE() {
        return NODETYPE;
    }
    
    
    
    public boolean checkElement(EvaluationChainElement anotherElement){
        if(!anotherElement.getE().equals(this.getE())) return false;
        
        if(!anotherElement.getR().equals(this.getR())) return false;
        
        return true;
    }
    
}
