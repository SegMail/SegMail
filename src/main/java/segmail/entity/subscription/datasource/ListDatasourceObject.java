/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author LeeKiatHaw
 */
public class ListDatasourceObject {
    
    private String email;
    
    private Map<String,Object> values;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void addValue(String field, Object value) {
        if(values == null)
            values = new HashMap<>();
        
        values.put(field, value);
    }
    
    public Object getValue(String field) {
        if(values == null)
            values = new HashMap<>();
        
        return values.get(field);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.email);
        hash = 47 * hash + Objects.hashCode(this.values);
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
        final ListDatasourceObject other = (ListDatasourceObject) obj;
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.values, other.values)) {
            return false;
        }
        return true;
    }
    
    
}
