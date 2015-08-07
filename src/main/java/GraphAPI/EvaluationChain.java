/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphAPI;

import eds.component.GenericObjectService;
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

    @Id @GeneratedValue(generator="EVALUATION_CHAIN_SEQ",strategy=GenerationType.TABLE) 
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
    
    /**
     * "Find [e] via [r], using current node as the [type] in [r], repeat this step if 
     * [RECURSIVE] is true."
     * 
     * @param e The EnterpriseObject class which is to be searched for as the next node.
     * @param r The EnterpriseRelationship class which is to be used to search for the next node.
     * @param type If SOURCE is selected, the current node will be used as a SOURCE
     * to search for the next node. Likewise for TARGET.
     * @param RECURSIVE If this is true, the Graph will explore all repeated objects 
     * until none is found before moving on the next evaluation criteria. If this
     * is false, then only 1 level will be explored.
     * @return 
     */
    public EvaluationChain addPathElement(Class<? extends EnterpriseObject> e, Class<? extends EnterpriseRelationship> r, NodeType type, boolean RECURSIVE){
        /**
         * When adding new elements, check if it is the same as the last element in the chain. If yes, set the previous element's 
         * recursive flag to true. In this way, we prevent redundancy and keep the chain clean
         */
        EvaluationChainElement newElement = new EvaluationChainElement(RECURSIVE,e,r,type);
        EvaluationChainElement lastElement = (elements.isEmpty()) ? null : elements.get(elements.size()-1);
        
        if(newElement.checkElement(lastElement)){
            lastElement.setRECURSIVE(true);
        } else {
            elements.add(newElement);
        }
        
        return this;
    }
    
    /**
     * Static helper method to iterate the evaluation chain link once and get a list of objects.
     * 
     * @param <S>
     * @param <E>
     * @param s
     * @param chainLink
     * @param objectService
     * @return 
     */
    public static <S extends EnterpriseObject,E extends EnterpriseObject> List<E> getObjects(
            S s,
            EvaluationChainElement chainLink,
            GenericObjectService objectService){
        
        List<E> targets = new ArrayList<>();
        NodeType type = chainLink.getNODETYPE();
        switch(type){
            case    SOURCE  : targets = objectService.getAllTargetObjectsFromSource(s.getOBJECTID(), chainLink.getR(), chainLink.getE());
                              break;
            case    TARGET  : targets = objectService.getAllSourceObjectsFromTarget(s.getOBJECTID(), chainLink.getR(), chainLink.getE());
                              break;
            default         : //If no nodetype is set, do not retrieve anything.
                              break;
        }
        
        return targets;
    }
    
    /**
     * Static helper method to iterate the evaluation chain link once and get a list of relationships.
     * 
     * @param <S>
     * @param <R>
     * @param s
     * @param chainLink
     * @param objectService
     * @return 
     */
    public static <S extends EnterpriseObject, R extends EnterpriseRelationship> List<R> getRelationships(
            S s,
            EvaluationChainElement chainLink,
            GenericObjectService objectService){
        
        List<R> relationships = new ArrayList<>();
        NodeType type = chainLink.getNODETYPE();
        switch(type){
            case    SOURCE  : relationships = objectService.getRelationshipsForSourceObject(s.getOBJECTID(),chainLink.getR());
                              break;
            case    TARGET  : relationships = objectService.getRelationshipsForTargetObject(s.getOBJECTID(), chainLink.getR());
                              break;
            default         : //If no nodetype is set, do not retrieve anything.
                              break;
        }
        
        return relationships;
    }
}
