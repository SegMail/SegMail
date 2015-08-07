/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphAPI;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import eds.entity.data.NodeType;

/**
 *
 * @author LeeKiatHaw
 */
public class EntityGraphImpl extends EntityGraph {
    
    /**
     * The evaluation chain will dictate how the EntityGraph should be explored.
     * It consists of individual chain elements that represents "steps" that are 
     * required for the chain to traverse to the next node/edge. This implementation
     * requires the setting of 
     */
    protected EvaluationChain chain;
    
    /**
     * The element pointer will tell the graph which part of the evaluation
     * chain it is in right now.
     */
    protected EvaluationChainElement elementPointer;
    
    /**
     * The node pointer will tell the graph which node it is currently pointing at 
     * right now. The first time the graph is initiated, it should be the root.
     */
    protected EnterpriseObject nodePointer;
    
    /**
     * The relationship pointer will tell the graph which relationship it is 
     * currently pointing at right now. The first time the graph is initiated,
     * it should be null;
     */
    protected EnterpriseRelationship relPointer;

    public EntityGraphImpl(EnterpriseObject e) {
        super(e);
        clearNextNodeTypes();
        resetToRoot();
    }

    @Override
    public <E extends EnterpriseObject, R extends EnterpriseRelationship> 
            EntityGraph setNextNodeType(Class<E> e, Class<R> r, NodeType type, boolean recursive) {
        
        chain.addPathElement(e, r, type, recursive);
        
        return this;
    }

    @Override
    public synchronized <R extends EnterpriseRelationship> R getNextEdge() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized <E extends EnterpriseObject> E getNextNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public final void resetToRoot() {
        //Set the elementPointer to root node.
        nodePointer = root;
        //Set the elementPointer to null
        elementPointer = null;
        //Set the relPointer to null
        relPointer = null;
    }

    @Override
    public final void clearNextNodeTypes() {
        chain = new EvaluationChain();
    }


}
