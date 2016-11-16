/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import seca2.component.landing.ServerNodeType;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SERVER_INSTANCE")
@EntityListeners({
    ServerIPResolverListener.class,
    ServerInstanceListener.class
})
@XmlRootElement
public class ServerInstance extends EnterpriseObject {
    
    /**
     * A simple human readable name for the server instance
     */
    private String NAME;
    
    /**
     * The IP address of the server instance
     */
    private String IP_ADDRESS;
    
    /**
     * The accessible port of the server instance
     */
    private int PORT;
    
    /**
     * The actual URI of the server location
     */
    private String URI;
    
    /**
     * The hostname of that the IP address is mapped to
     */
    private String HOSTNAME;
    
    /**
     * The path of the URL
     */
    private String PATH;
    
    
    private String SERVER_NODE_TYPE;
    

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getIP_ADDRESS() {
        return IP_ADDRESS;
    }

    public void setIP_ADDRESS(String IP_ADDRESS) {
        this.IP_ADDRESS = IP_ADDRESS;
    }

    public String getHOSTNAME() {
        return HOSTNAME;
    }

    public void setHOSTNAME(String HOSTNAME) {
        this.HOSTNAME = HOSTNAME;
    }

    public String getSERVER_NODE_TYPE() {
        return SERVER_NODE_TYPE;
    }

    public void setSERVER_NODE_TYPE(String SERVER_NODE_TYPE) {
        this.SERVER_NODE_TYPE = SERVER_NODE_TYPE;
    }
    
    public void setSERVER_NODE_TYPE(ServerNodeType SERVER_NODE_TYPE) {
        this.SERVER_NODE_TYPE = SERVER_NODE_TYPE.value;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public String getPATH() {
        return PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
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
        return this.NAME;
    }
    
}
