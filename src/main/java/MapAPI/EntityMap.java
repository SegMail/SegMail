/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapAPI;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author LeeKiatHaw
 * @param <E>
 * @param <R>
 */
public class EntityMap<E extends EnterpriseObject, R extends EnterpriseRelationship> implements Iterable<Node<E>> {
    
    private List<Node<E>> nodes;
    
    private List<Edge> sourceEdges;
    
    private List<Edge> targetEdges;
    
    public EntityMap(List<E> objects, Class<R> r){
        this.nodes = new ArrayList();
        this.sourceEdges = new ArrayList();
        this.targetEdges = new ArrayList();
        
        for(EnterpriseObject o : objects){
            Node<E> newNode = new Node(o);
            newNode.initSourceEdges(r);
            newNode.initTargetEdges(r);
            
            this.nodes.add(newNode);
            this.sourceEdges.addAll(newNode.getSourceEdges(r));
            this.targetEdges.addAll(newNode.getTargetEdges(r));
        }
    }
    
    public void init(){
        
    }
    
    public List<Node<E>> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node<E>> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getSourceEdges() {
        return sourceEdges;
    }

    public List<Edge> getTargetEdges() {
        return targetEdges;
    }

    @Override
    public Iterator<Node<E>> iterator() {
        return this.nodes.iterator();
    }

    public int getNodeSize(){
        return nodes.size();
    }
    
    public int getTotalEdgeSize(){
        return getSourceEdgeSize() + getTargetEdgeSize();
    }
    
    public int getSourceEdgeSize(){
        return sourceEdges.size();
    }
    
    public int getTargetEdgeSize(){
        return targetEdges.size();
    }
}
