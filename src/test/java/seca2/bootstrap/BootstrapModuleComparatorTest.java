/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import seca2.bootstrap.module.Client.ClientModule;
import seca2.bootstrap.module.Program.ProgramModule;
import seca2.bootstrap.module.User.UserModule;
import seca2.bootstrap.module.rewrite.RewriteModule;
import segmail.bootstrap.module.Subscription.SubscriptionModule;

/**
 *
 * @author LeeKiatHaw
 */
public class BootstrapModuleComparatorTest {
    
    BootstrappingChainFactory factory = new BootstrappingChainFactory();
    
    BootstrapModule user = new UserModule();//factory.getModuleByName("UserModule");
    BootstrapModule rewrite = new RewriteModule(); //factory.getModuleByName("RewriteModule");
    BootstrapModule program = new ProgramModule(); //factory.getModuleByName("ProgramModule");
    BootstrapModule client = new ClientModule(); //noncore
    BootstrapModule subscribe = new SubscriptionModule(); //noncore

    BootstrapModuleComparator c = factory.getComparator();
    
    public BootstrapModuleComparatorTest() {
        
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
     * Test of compare method, of class BootstrapModuleComparator.
     */
    @Test
    public void testCompareCores() {
        
        assertTrue(c.compare(user, user)==0);
        assertTrue(c.compare(rewrite, rewrite)==0);
        assertTrue(c.compare(program, program)==0);
        assertTrue(c.compare(user, rewrite) < 0);
        assertTrue(c.compare(program, user) > 0);
        assertTrue(c.compare(program, rewrite) > 0);
        
    }
    
    @Test
    public void testCompareNonCores() {
        
        assertTrue(c.compare(client, client)==0);
        assertTrue(c.compare(subscribe, subscribe)==0);
        assertTrue(c.compare(client, subscribe) == 0);
        assertTrue(c.compare(subscribe, client) == 0);
        
    }
    
    @Test
    public void testCompareCoresNonCores() {
        
        assertTrue(c.compare(client, user) > 0);
        assertTrue(c.compare(subscribe, user) > 0);
        assertTrue(c.compare(client, rewrite) > 0);
        assertTrue(c.compare(subscribe, rewrite) > 0);
        assertTrue(c.compare(client, program) > 0);
        assertTrue(c.compare(subscribe, program) > 0);
        
        assertTrue(c.compare(user, client) < 0);
        assertTrue(c.compare(user, subscribe) < 0);
        assertTrue(c.compare(rewrite, client) < 0);
        assertTrue(c.compare(rewrite, subscribe) < 0);
        assertTrue(c.compare(program, client) < 0);
        assertTrue(c.compare(program, subscribe) < 0);
    }
}
