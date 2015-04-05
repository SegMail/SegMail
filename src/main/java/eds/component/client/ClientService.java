/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.client;

import eds.component.GenericEnterpriseObjectService;
import eds.component.data.DBConnectionException;
import eds.entity.client.Client;
import eds.entity.client.ClientType;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ClientService {
    
    @EJB private GenericEnterpriseObjectService genericEnterpriseObjectService;
    
    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Client getClientById(long clientid) throws DBConnectionException{
        try{
            //Try the shorter JPA way
            //Client result = em.find(Client.class, clientid);
            
            Client result = this.genericEnterpriseObjectService.getEnterpriseObjectById(clientid, Client.class);
            return result;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ClientType getClientTypeByName(String clienttypename) throws DBConnectionException{
        try{
            
            List<ClientType> results = this.genericEnterpriseObjectService.getEnterpriseObjectsByName(clienttypename, ClientType.class);
            
            if(results == null || results.isEmpty())
                return null;
            
            //Return only the first matching result            
            return results.get(0);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerClientType(String clienttypename, String clienttypedesc)
        throws DBConnectionException, ClientTypeRegistrationException{
        
        try{
            //Validate the inputted clienttypename
            if(clienttypename == null || clienttypename.isEmpty())
                throw new ClientTypeRegistrationException("Client type name cannot be empty!");
            
            //Validate if client type already exist
            ClientType existingClientType = this.getClientTypeByName(clienttypename);
            if(existingClientType != null)
                throw new ClientTypeRegistrationException("Client type "+clienttypename+" already exists!");
            
            ClientType newClientType = new ClientType();
            newClientType.setCLIENT_TYPE_NAME(clienttypename);
            newClientType.setDESCRIPTION(clienttypedesc);
            
            this.em.persist(newClientType);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}
