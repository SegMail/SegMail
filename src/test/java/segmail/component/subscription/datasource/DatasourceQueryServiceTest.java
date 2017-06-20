/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.datasource.DATASOURCE_ENDPOINT_TYPE;
import segmail.entity.subscription.datasource.ListDataMapping;
import segmail.entity.subscription.datasource.ListDatasource;
import segmail.entity.subscription.datasource.ListDatasourceObject;
import segmail.entity.subscription.datasource.synchronize.ListDatasourceObjectWrapper;

/**
 *
 * @author LeeKiatHaw
 */
public class DatasourceQueryServiceTest {
    
    /**
     * Test DB info
     */
    final String SERVER_NAME = "mysql.airnavsystems.com";
    final String ENDPOINT_TYPE = DATASOURCE_ENDPOINT_TYPE.MYSQL.name;
    final String USERNAME = "kiathaw";
    final String PASSWORD = "sin123";
    final String DB_NAME = "ansys";
    final String TABLE_NAME = "newsletter_subscribers";
    final String KEY_FIELD = "email";
    
    /**
     * Required test services
     */
    DatasourceQueryService dsQueryService = new DatasourceQueryService();
    
    /**
     * Required test entities
     */
    ListDatasource ld;
    List<ListDataMapping> mappings;
    
    /**
     * Required test objects
     */
    Connection conn;
    
    /**
     * Test data
     */
    Map<String,String> fieldMappings = new HashMap<>();
    
    public DatasourceQueryServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        ld = new ListDatasource(
                "Test datasource", 
                ENDPOINT_TYPE, 
                SERVER_NAME,
                DB_NAME,
                "Test datasource",
                USERNAME,
                PASSWORD,
                TABLE_NAME,
                KEY_FIELD);
        
        //fieldMappings.put("EMAIL", KEY_FIELD);
        fieldMappings.put("FIRSTNAME", "fname");
        fieldMappings.put("LASTNAME", "lname");
        
        mappings = ListDataMapping.initMappingsFromMap(fieldMappings);
        mappings.add(new ListDataMapping("EMAIL",SubscriptionService.DEFAULT_EMAIL_FIELD_NAME,KEY_FIELD,FIELD_TYPE.EMAIL.name));
    }
    
    @After
    public void tearDown() throws SQLException {
        if(conn != null) {
            conn.close();
        }
    }

    /**
     * Test of getRemoteSubscribers method, of class DatasourceQueryService.
     */
    @Test
    public void testGetRemoteSubscribers1() throws Exception {
        List<ListDatasourceObject> objs = dsQueryService.getRemoteSubscribers(ld, mappings, KEY_FIELD,null, 0, 10);
        assertEquals(objs.size(),10);
    }
    
    /**
     * Test if 2 calls to the same database are producing consistent results
     * 
     * @throws Exception 
     */
    @Test
    public void testGetRemoteSubscribers2() throws Exception {
        List<ListDatasourceObject> objs1 = dsQueryService.getRemoteSubscribers(ld, mappings, KEY_FIELD,null, 0, 5);
        List<ListDatasourceObject> objs2 = dsQueryService.getRemoteSubscribers(ld, mappings, KEY_FIELD,new ArrayList<String>(), 2, 3);
        
        assertTrue(objs1.containsAll(objs2));
    }
    
    

    /**
     * Test of getRemoteSubscriberWrappers method, of class DatasourceQueryService.
     */
    @Test
    public void testGetRemoteSubscriberWrappers() throws Exception {
        List<ListDatasourceObjectWrapper> objs1 = dsQueryService.getRemoteSubscriberWrappers(ld, mappings, KEY_FIELD,null, 0, 5);
        List<ListDatasourceObjectWrapper> objs2 = dsQueryService.getRemoteSubscriberWrappers(ld, mappings, KEY_FIELD,new ArrayList<String>(), 0, 5);
        
        assertTrue(objs1.containsAll(objs2));
    }
    
}
