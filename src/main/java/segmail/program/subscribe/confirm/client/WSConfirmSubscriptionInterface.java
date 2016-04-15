
package segmail.program.subscribe.confirm.client;

import eds.component.webservice.ExpiredTransactionException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import seca2.bootstrap.module.Webservice.client.GenericWSInterface;
import eds.component.webservice.TransactionProcessedException;
import eds.component.webservice.UnwantedAccessException;


/**
 * This class was generated by the JAX-WS RI. 
 * JAX-WS RI 2.2.8
 * Generated source version: 2.2
 * 
 * This class is for client, not server endpoint. It should be deployed on the 
 * client side.
 * 
 */
@WebService(
        name = "WSConfirmSubscription", 
        targetNamespace = "http://client.confirm.subscribe.program.segmail/")
public interface WSConfirmSubscriptionInterface extends GenericWSInterface {

    /**
     * 
     * @param key
     * @return
     *     returns java.lang.String
     * @throws eds.component.webservice.TransactionProcessedException
     * @throws eds.component.webservice.UnwantedAccessException
     * @throws eds.component.webservice.ExpiredTransactionException
     */
    @WebMethod
    public String confirm(@WebParam(name = "key") String key)
            throws TransactionProcessedException, UnwantedAccessException, ExpiredTransactionException;
            
}
