/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.List;
import javax.enterprise.inject.Instance;
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
public class BootstrappingChainFactoryTest {
    
    public BootstrappingChainFactoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class BootstrappingChainFactory.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCoreHead method, of class BootstrappingChainFactory.
     */
    @Test
    public void testGetCoreHead() {
        System.out.println("getCoreHead");
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        BootstrapModule expResult = null;
        BootstrapModule result = instance.getCoreHead();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNonCoreHead method, of class BootstrappingChainFactory.
     */
    @Test
    public void testGetNonCoreHead() {
        System.out.println("getNonCoreHead");
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        BootstrapModule expResult = null;
        BootstrapModule result = instance.getNonCoreHead();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCoreBootstrapModuleList method, of class BootstrappingChainFactory.
     */
    @Test
    public void testGetCoreBootstrapModuleList() {
        System.out.println("getCoreBootstrapModuleList");
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        List<BootstrapModule> expResult = null;
        List<BootstrapModule> result = instance.getCoreBootstrapModuleList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNonCoreBootstrapModuleList method, of class BootstrappingChainFactory.
     */
    @Test
    public void testGetNonCoreBootstrapModuleList() {
        System.out.println("getNonCoreBootstrapModuleList");
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        List<BootstrapModule> expResult = null;
        List<BootstrapModule> result = instance.getNonCoreBootstrapModuleList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllBootstrapModuleList method, of class BootstrappingChainFactory.
     */
    @Test
    public void testGetAllBootstrapModuleList() {
        System.out.println("getAllBootstrapModuleList");
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        List<BootstrapModule> expResult = null;
        List<BootstrapModule> result = instance.getAllBootstrapModuleList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateBootstrapList method, of class BootstrappingChainFactory.
     */
    @Test
    public void testGenerateBootstrapList() {
        System.out.println("generateBootstrapList");
        Instance<BootstrapModule> modules = null;
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        List<BootstrapModule> expResult = null;
        List<BootstrapModule> result = instance.generateBootstrapList(modules);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of constructBoostrapChain method, of class BootstrappingChainFactory.
     */
    @Test
    public void testConstructBoostrapChain() {
        System.out.println("constructBoostrapChain");
        List<BootstrapModule> moduleList = null;
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        BootstrapModule expResult = null;
        BootstrapModule result = instance.constructBoostrapChain(moduleList);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModuleByName method, of class BootstrappingChainFactory.
     */
    @Test
    public void testGetModuleByName() {
        System.out.println("getModuleByName");
        String name = "";
        BootstrappingChainFactory instance = new BootstrappingChainFactory();
        BootstrapModule expResult = null;
        BootstrapModule result = instance.getModuleByName(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
