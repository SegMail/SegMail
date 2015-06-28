/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapAPI;

import eds.component.GenericEnterpriseObjectService;
import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

/**
 *
 * @author LeeKiatHaw
 * @param <E>
 * @param <T>
 */
public class Node<E extends EnterpriseObject> {
    
    @EJB private GenericEnterpriseObjectService objectService;
    
    private E e;
    
    private Map<Class<? extends EnterpriseRelationship>,List<Edge>> sourceEdges;
    
    private Map<Class<? extends EnterpriseRelationship>,List<Edge>> targetEdges;

    public Node(E e) {
        this.e = e;
        sourceEdges = new HashMap();
        targetEdges = new HashMap();
    }

    public E getE() {
        return e;
    }
    
    public <R extends EnterpriseRelationship> List<Edge> getSourceEdges(Class<R> r){
        return sourceEdges.get(r);
    }
    
    public <R extends EnterpriseRelationship> List<Edge> getTargetEdges(Class<R> r){
        return targetEdges.get(r);
    }
    
    /**
     * Searches and build all edges where node is a source.
     * 
     * @param <R>
     * @param r 
     */
    public <R extends EnterpriseRelationship> void initSourceEdges(Class<R> r){
        // 1. Get the relationship objects
        List<R> newRel = objectService.getRelationshipsForSourceObject(e.getOBJECTID(), r);
        
        // 2. Build the edges 
        List<Edge> newEdges = new ArrayList();
        
        for(EnterpriseRelationship rel : newRel){
            Edge newEdge = new Edge(rel);
            newEdges.add(newEdge);
        }
        
        // 3. Add in to this node's map
        if(sourceEdges.containsKey(r)){
            sourceEdges.remove(r);
        }
        sourceEdges.put(r, newEdges);
    }
    
    /**
     * Searches and build all edges where node is a target.
     * 
     * @param <R>
     * @param r 
     */
    public <R extends EnterpriseRelationship> void initTargetEdges(Class<R> r){
        // 1. Get the relationship objects
        List<R> newRel = objectService.getRelationshipsForTargetObject(e.getOBJECTID(), r);
        
        // 2. Build the edges 
        List<Edge> newEdges = new ArrayList<Edge>();
        
        for(EnterpriseRelationship rel : newRel){
            Edge newEdge = new Edge(rel);
            newEdges.add(newEdge);
        }
        
        // 3. Add in to this node's map
        if(targetEdges.containsKey(r)){
            targetEdges.remove(r);
        }
        targetEdges.put(r, newEdges);
    }
}
