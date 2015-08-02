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
    
    private EvaluationChain path;

    public EntityGraphImpl(EnterpriseObject e) {
        super(e);
        path = new EvaluationChain();
    }

    @Override
    public <E extends EnterpriseObject, R extends EnterpriseRelationship> EntityGraph setNextNodeType(Class<E> e, Class<R> r, boolean recursive, NodeType type) {
        
        path.addPathElement(recursive, e, r, type);
        
        return this;
    }

    @Override
    public <R extends EnterpriseRelationship> R getNextEdge() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <E extends EnterpriseObject> E getNextNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
