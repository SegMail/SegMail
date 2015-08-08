/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EDS;

import eds.component.StaticObjectService;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * http://www.hascode.com/2012/04/arquillian-tutorial-writing-java-ee-6-integration-tests-and-more/
 *
 * @author LeeKiatHaw
 */

public class StaticObjectServicesTest {

    private Context context;
    private StaticObjectService sObjectService;
    public final static String OBJECT_NAME = "StaticObjectService";
    
    @Test
    public void testContainer() throws NamingException{
        //EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        Map<Object, Object> properties = new HashMap<Object, Object>(); 
        properties.put(EJBContainer.APP_NAME, "SegMail");
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
        EJBContainer container = EJBContainer.createEJBContainer(properties);
        sObjectService = (StaticObjectService)container.getContext().lookup("java:global/classes/StaticObjectService");
        
        Assert.assertNotNull(sObjectService);
    }
    
}
