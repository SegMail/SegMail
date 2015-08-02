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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EVALUATION_CHAIN")
@TableGenerator(name="EVALUATION_CHAIN_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
public class EvaluationChain implements Serializable {
    
    private long PATH_ID;
    
    private List<EvaluationChainElement> elements = new ArrayList<EvaluationChainElement>();

    @Id @GeneratedValue(generator="EVALUATION_PATH_SEQ",strategy=GenerationType.TABLE) 
    public long getPATH_ID() {
        return PATH_ID;
    }

    public void setPATH_ID(long PATH_ID) {
        this.PATH_ID = PATH_ID;
    }

    @OneToMany(targetEntity=EvaluationChainElement.class)
    public List<EvaluationChainElement> getElements() {
        return elements;
    }

    public void setElements(List<EvaluationChainElement> elements) {
        this.elements = elements;
    }
    
    public EvaluationChain addPathElement(boolean RECURSIVE, Class<? extends EnterpriseObject> e, Class<? extends EnterpriseRelationship> r, NodeType type){
        /**
         * When adding new elements, check if it is the same as the last element in the chain. If yes, set the previous element's 
         * recursive flag to true. In this way, we prevent redundancy and keep the chain clean
         */
        EvaluationChainElement newElement = new EvaluationChainElement(RECURSIVE,e,r,type);
        EvaluationChainElement lastElement = elements.get(elements.size()-1);
        
        if(newElement.checkElement(lastElement)){
            lastElement.setRECURSIVE(true);
        } else {
            elements.add(newElement);
        }
        
        return this;
    }
    
    
}
