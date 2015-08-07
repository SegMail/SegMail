/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EDS;

import eds.component.StaticObjectService;
import eds.entity.data.EnterpriseObject;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import seca2.bootstrap.module.User.UserContainer;

/**
 * http://www.hascode.com/2012/04/arquillian-tutorial-writing-java-ee-6-integration-tests-and-more/
 *
 * @author LeeKiatHaw
 */
@RunWith(Arquillian.class)
public class StaticObjectServicesTest {

    @Inject
    //@EJB
    StaticObjectService sObjService;
    final String STATIC_SERVICE_DIRECTORY = "eds.component.StaticObjectService";
    final String STATIC_SERVICE_JNDI = "java:global/classes/StaticObjectService";

    static EJBContainer ejbContainer;

    public StaticObjectServicesTest() throws NamingException {

        // Create initial context
    }

    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(StaticObjectService.class)
                .addDefaultPackage()
                .addAsResource("META-INF/persistence.xml");
                //.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testGetAllEntities() {
        List<Class<? extends EnterpriseObject>> allEntities = sObjService.getAllEnterpriseObjects();
        Assert.assertTrue(allEntities.size() > 0);
    }
}
