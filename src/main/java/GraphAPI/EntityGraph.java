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
 * The EntityGraph implements a adjacency list to represent a graph of <E extends EnterpriseObject>
 * connected with <R extends EnterpriseRelationship> edges. 
 * 
 * Incremental buffer approach to loading, not load the entire database at once.
 * 
 * A graph can contain many types of objects, so it would not make sense to define
 * generics.
 * 
 * 
 * @author LeeKiatHaw
 */
public abstract class EntityGraph {
    
    protected EnterpriseObject root;
    
    
    protected EntityGraph(EnterpriseObject e){
        root = e;
    }
    
    /**
     * Set the next object to be retrieved.
     * <p>
     * "Find [e] via [r], using current node as the [type] in [r], repeat this step if 
     * [RECURSIVE] is true."
     * 
     * @param e The EnterpriseObject class which is to be searched for as the next node.
     * @param r The EnterpriseRelationship class which is to be used to search for the next node.
     * @param recursive If this is true, the Graph will explore all repeated objects 
     * until none is found before moving on the next evaluation criteria. If this
     * is false, then only 1 level will be explored.
     * @param type If SOURCE is selected, the current node will be used as a SOURCE
     * to search for the next node. Likewise for TARGET.
     * 
     * @return The current EntityGraph instance
     */
    public abstract <E extends EnterpriseObject,R extends EnterpriseRelationship> EntityGraph 
        setNextNodeType(Class<E> e, Class<R> r, NodeType type, boolean recursive);
    
    /**
     * This is a factory method that returns an EntityGraph implementation.
     * 
     * @param root
     * @return 
     */
    public static EntityGraph getEntityGraph(EnterpriseObject root){
        return new EntityGraphImpl(root);
    }
    
    /**
     * Iterative method to get the next Edge to be explored. Returns null if
     * there is no more next edge.
     * 
     * @param <R>
     * @return 
     */
    public abstract <R extends EnterpriseRelationship> R getNextEdge();
    
    /**
     * Iterative method to get the next Node to be explored. Returns null if
     * there is no more next node.
     * 
     * @param <E>
     * @return 
     */
    public abstract <E extends EnterpriseObject> E getNextNode();
    
    /**
     * Reset the internal pointers to the root node and start over again.
     */
    public abstract void resetToRoot();
    
    /**
     * Clear all next node types.
     */
    public abstract void clearNextNodeTypes();
}
