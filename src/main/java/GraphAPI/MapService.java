/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphAPI;

import eds.component.GenericObjectService;
import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MapService {
    
    @EJB private GenericObjectService objectService;
    
    /*
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <E extends EnterpriseObject,R extends EnterpriseRelationship> Node<E> 
        generateNode(E e, Class<R> r ){
        Node<E> newNode = new Node(e);
        
        List<R> newSourceRel = objectService.getRelationshipsForSourceObject(e.getOBJECTID(), r);
        List<R> newTargetRel = objectService.getRelationshipsForTargetObject(e.getOBJECTID(), r);
        
        for(R rel : newSourceRel){
            newNode.addEdge(rel);
        }
        
        for(R rel : newTargetRel){
            newNode.addEdge(rel);
        }
        
        return newNode;
    }*/
}
