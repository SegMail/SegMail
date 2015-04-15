/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.subscription;

import eds.component.GenericEnterpriseObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EnterpriseObjectNotFoundException;
import eds.entity.client.Client;
import eds.entity.subscription.ListAssignment;
import eds.entity.subscription.SubscriptionList;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;
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
public class SubscriptionService {
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;
    
    @EJB private GenericEnterpriseObjectService genericEntepriseObjectService;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addList(long clientid, String listname, boolean remote) 
            throws DBConnectionException, EnterpriseObjectNotFoundException{
        try{
            //1. Create the list object and persist it first
            SubscriptionList newList = new SubscriptionList();
            newList.setLIST_NAME(listname); //Right now we don't keep history
            
            em.persist(newList);
            
            //2. Create the assignment to the client object
            Client client = this.genericEntepriseObjectService.getEnterpriseObjectById(clientid, Client.class);
            if (client == null)
                throw new EnterpriseObjectNotFoundException(clientid);
            //Test at this point whethe the newList object still gets persisted
            
            ListAssignment listAssignment = new ListAssignment(newList,client);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex){
            //Wrap it in an unchecked exception so that it will rollback
            throw ex;
        }
    }
    
}
