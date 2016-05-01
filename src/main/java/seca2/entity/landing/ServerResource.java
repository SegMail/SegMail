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
public class ServerResource extends EnterpriseData<ServerInstance> {

    private String RESOURCE_TYPE;
    
    private String RESOURCE_NAME;
    
    private String JNDI_NAME;
    
    //List of params for additional properties

    public String getRESOURCE_TYPE() {
        return RESOURCE_TYPE;
    }

    public void setRESOURCE_TYPE(String RESOURCE_TYPE) {
        this.RESOURCE_TYPE = RESOURCE_TYPE;
    }
    
    public void setRESOURCE_TYPE(ServerResourceType RESOURCE_TYPE) {
        this.RESOURCE_TYPE = RESOURCE_TYPE.label;
    }

    public String getRESOURCE_NAME() {
        return RESOURCE_NAME;
    }

    public void setRESOURCE_NAME(String RESOURCE_NAME) {
        this.RESOURCE_NAME = RESOURCE_NAME;
    }

    public String getJNDI_NAME() {
        return JNDI_NAME;
    }

    public void setJNDI_NAME(String JNDI_NAME) {
        this.JNDI_NAME = JNDI_NAME;
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
