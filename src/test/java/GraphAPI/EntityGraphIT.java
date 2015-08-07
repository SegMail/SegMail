/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GraphAPI;

import eds.component.GenericObjectService;
import eds.component.StaticObjectService;
import eds.component.data.HibernateEMServices;
import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import eds.entity.data.NodeType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author LeeKiatHaw
 */
public class EntityGraphIT {
    
    /**
     * The number of objects to be tested.
     */
    int numObjects = 1000;
    
    /**
     * The list of EnterpriseObjects to be tested as nodes.
     */
    List<? extends EnterpriseObject> objectPoolMixed;
    
    List<? extends EnterpriseRelationship> edgesPoolMixed;
    
    InitialContext EJBContext;
    /**
     * You need this to generate the metamodel and the available entity classes.
     */
    @EJB HibernateEMServices hibernateDBServices;
    final String DB_SERVICE_DIRECTORY = "eds.component.data.HibernateEMServices";
    @EJB GenericObjectService objectServices;
    final String GENERIC_SERVICE_DIRECTORY = "eds.component.GenericEnterpriseObjectService";
    @EJB StaticObjectService statisObjectServices;
    final String STATIC_SERVICE_DIRECTORY = "eds.component.StaticObjectService";
    
    public EntityGraphIT() throws NamingException {
        this.EJBContext = new InitialContext();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws NamingException {
        objectPoolMixed = new ArrayList<>();
        //Set up all objects using HibernateEMServices and reflection
        if(statisObjectServices == null){
            //If @EJB injection doesn't work, use InitialContext to create the EJBs
            statisObjectServices = (StaticObjectService) EJBContext.lookup(STATIC_SERVICE_DIRECTORY);
        }
        List<Class<? extends EnterpriseObject>> allEO = statisObjectServices.getAllEnterpriseObjects();
                
        for(int i=0; i < numObjects; i++){
            
        }
    }
    
    @After
    public void tearDown() {
    }
 
    
}
