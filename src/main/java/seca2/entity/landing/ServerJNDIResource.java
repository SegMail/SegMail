/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SERVER_RESOURCE")
public class ServerJNDIResource extends EnterpriseData<ServerInstance> {

    private String RESOURCE_TYPE;
    
    private String RESOURCE_KEY;
    
    private String RESOURCE_VALUE;

    public String getRESOURCE_TYPE() {
        return RESOURCE_TYPE;
    }

    public void setRESOURCE_TYPE(String RESOURCE_TYPE) {
        this.RESOURCE_TYPE = RESOURCE_TYPE;
    }

    public String getRESOURCE_KEY() {
        return RESOURCE_KEY;
    }

    public void setRESOURCE_KEY(String RESOURCE_KEY) {
        this.RESOURCE_KEY = RESOURCE_KEY;
    }

    public String getRESOURCE_VALUE() {
        return RESOURCE_VALUE;
    }

    public void setRESOURCE_VALUE(String RESOURCE_VALUE) {
        this.RESOURCE_VALUE = RESOURCE_VALUE;
    }
    
    
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String HTMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
