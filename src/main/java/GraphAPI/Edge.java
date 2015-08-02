/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphAPI;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.r);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge<?> other = (Edge<?>) obj;
        if (!Objects.equals(this.r, other.r)) {
            return false;
        }
        return true;
    }
    
    
}
