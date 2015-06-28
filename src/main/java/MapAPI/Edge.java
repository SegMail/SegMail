/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapAPI;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;

/**
 *
 * @author LeeKiatHaw
 * @param <R>
 * @param <S>
 * @param <T>
 */
public class Edge<R extends EnterpriseRelationship> {
    
    public final R r;

    public Edge(R r) {
        this.r = r;
    }

    public R getR() {
        return r;
    }
    
    public EnterpriseObject getSource(){
        return r.getSOURCE();
    }
    
    public EnterpriseObject getTarget(){
        return r.getTARGET();
    }
}
