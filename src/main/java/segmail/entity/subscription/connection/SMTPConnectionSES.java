/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.connection;

import com.amazonaws.services.ec2.model.Region;
import eds.entity.resource.AbstractConnection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author LeeKiatHaw
 */
public class SMTPConnectionSES extends AbstractConnection{

    private Region REGION;
    

    @Enumerated(EnumType.STRING)
    public Region getREGION() {
        return REGION;
    }

    public void setREGION(Region REGION) {
        this.REGION = REGION;
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
    public String alias() {
        return "SMTPConnectionSES";
    }

    @Override
    public void ping() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
