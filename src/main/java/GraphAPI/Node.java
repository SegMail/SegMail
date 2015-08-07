/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphAPI;

import eds.component.GenericObjectService;
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
    
    @EJB private GenericObjectService objectService;
    
    private E e;
    
    private Map<Class<? extends EnterpriseRelationship>,List<Edge>> sourceEdges;
    
    private Map<Class<? extends EnterpriseRelationship>,List<Edge>> targetEdges;
    
    public Node(GenericObjectService objectService){
        this.setObjectService(objectService);
    }

    public Node(E e,GenericObjectService objectService) {
        this(objectService);
        this.e = e;
        sourceEdges = new HashMap();
        targetEdges = new HashMap();
        
        //this.objectService = objectService;
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
    
    public <R extends EnterpriseRelationship> void addEdge(R r){
        EnterpriseObject source = r.getSOURCE();
        EnterpriseObject target = r.getTARGET();
        
        /**
         * There could be repeated edges eg. self-directed edges.
         */
        if(source.equals(e)){
            this.addSourceEdge(r);
        }
        if(target.equals(e)){
            this.addTargetEdge(r);
        }
    }

    public <R extends EnterpriseRelationship> void addSourceEdge(R r){
        List<Edge> sourceEdgeList = this.sourceEdges.get(r.getClass());
        if(sourceEdgeList == null){
            sourceEdgeList = new ArrayList<Edge>();
            this.sourceEdges.put(r.getClass(), sourceEdgeList);
        }
        Edge<R> newEdge = new Edge(r);
        if(!sourceEdgeList.contains(newEdge))
        sourceEdgeList.add(newEdge);
    }
    
    public <R extends EnterpriseRelationship> void addTargetEdge(R r){
        List<Edge> targetEdgeList = this.targetEdges.get(r.getClass());
        if(targetEdgeList == null){
            targetEdgeList = new ArrayList<Edge>();
            this.targetEdges.put(r.getClass(), targetEdgeList);
        }
        Edge<R> newEdge = new Edge(r);
        if(!targetEdgeList.contains(newEdge))
        targetEdgeList.add(new Edge(r));
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

    public GenericObjectService getObjectService() {
        return objectService;
    }

    public void setObjectService(GenericObjectService objectService) {
        this.objectService = objectService;
    }
    
    
}
