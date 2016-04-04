
package segmail.program.subscribe.confirm.client;

import eds.component.data.RelationshipNotFoundException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


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
        targetNamespace = "http://webservice.confirm.subscribe.program.segmail/")
@SOAPBinding(style = Style.RPC)
public interface WSConfirmSubscriptionInterface {


    /**
     * 
     * @param key
     * @return
     *     returns java.lang.String
     * @throws eds.component.data.RelationshipNotFoundException
     */
    @WebMethod
    public String confirm(@WebParam(name = "key") String key) 
            throws RelationshipNotFoundException;

}
