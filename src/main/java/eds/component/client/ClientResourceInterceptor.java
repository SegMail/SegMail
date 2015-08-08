/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.client;

import eds.entity.client.ClientResource;
import eds.entity.data.EnterpriseObject;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * [2015.07.12] Not a good idea to do business specific processing in an interceptor
 * this is only good for backend tech stuff like logging and audit.
 * But we now know it works!
 * Then again, we can inject the ClientFacade in every Service that requires clients!
 * @author LeeKiatHaw
 */

public class ClientResourceInterceptor {
    
    @Inject ClientFacade clientFacade;
    @EJB ClientService clientService;
    
    //@AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        //do something before EJB call
        
        
        Object result = context.proceed();
        
        //do something after EJB call
        if(result.getClass().isAnnotationPresent(ClientResource.class) && result instanceof EnterpriseObject){
            clientService.assignClientResource(clientFacade.getClient(), (EnterpriseObject) result);
        }
        
        return result;
    }
}
