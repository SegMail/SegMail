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
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author LeeKiatHaw
 * @param <E>
 * @param <R>
 */
public class EntityMap<E extends EnterpriseObject, R extends EnterpriseRelationship> {
    
    private List<Node<E>> nodes;
    
    private List<Edge> sourceEdges;
    
    private List<Edge> targetEdges;
    
    public EntityMap(List<E> objects, Class<R> r){
        this.sourceEdges = new ArrayList();
        this.targetEdges = new ArrayList();
        
        for(EnterpriseObject o : objects){
            Node<E> newNode = new Node(o);
            newNode.initSourceEdges(r);
            newNode.initTargetEdges(r);
            
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

    
    
}
