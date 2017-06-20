/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource.synchronize;

import java.util.Objects;
import segmail.entity.subscription.datasource.ListDatasourceObject;

/**
 *
 * @author LeeKiatHaw
 */
public class ListDatasourceObjectWrapper extends SyncListObject
    implements Comparable<ListDatasourceObjectWrapper>
{
    
    private ListDatasourceObject dsObj;

    public ListDatasourceObjectWrapper(ListDatasourceObject dsObj) {
        this.dsObj = dsObj;
    }

    @Override
    public String getId() {
        return dsObj.getEmail();
    }

    public ListDatasourceObject getDsObj() {
        return dsObj;
    }

    public void setDsObj(ListDatasourceObject dsObj) {
        this.dsObj = dsObj;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.dsObj);
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
        final ListDatasourceObjectWrapper other = (ListDatasourceObjectWrapper) obj;
        if (!Objects.equals(this.dsObj, other.dsObj)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ListDatasourceObjectWrapper o) {
        if (getDsObj() == null) return -1;
        if (o.getDsObj() == null) return 1;
        
        return getDsObj().getEmail().compareTo(o.getDsObj().getEmail());
    }
    
    
}
