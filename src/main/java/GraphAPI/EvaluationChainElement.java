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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This class exist for the purpose of a data model for the evaluation chain.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EVALUATION_CHAIN_ELEMENT")
public class EvaluationChainElement<E extends EnterpriseObject,R extends EnterpriseRelationship> implements Serializable {
    
    private Long ID;
    private boolean RECURSIVE;
    private Class<E> e;
    private Class<R> r;
    private NodeType NODETYPE;
    
    public EvaluationChainElement(){
        
    }

    public EvaluationChainElement(boolean RECURSIVE, Class<E> e, Class<R> r,NodeType type) {
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

    public NodeType getNODETYPE() {
        return NODETYPE;
    }

    public void setE(Class<E> e) {
        this.e = e;
    }

    public void setR(Class<R> r) {
        this.r = r;
    }

    public void setNODETYPE(NodeType NODETYPE) {
        this.NODETYPE = NODETYPE;
    }

    @ManyToOne(targetEntity=EnterpriseObject.class)
    public Class<E> getE() {
        return e;
    }

    @ManyToOne(targetEntity=EnterpriseRelationship.class)
    public Class<R> getR() {
        return r;
    }
    
    
    public boolean checkElement(EvaluationChainElement anotherElement){
        
        if(anotherElement == null) return false;
        
        if(!anotherElement.getE().equals(this.getE())) return false;
        
        if(!anotherElement.getR().equals(this.getR())) return false;
        
        return true;
    }
    
}
