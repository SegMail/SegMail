/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.link;

import eds.component.link.LinkParser;
import java.util.List;
import java.util.Map;
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
public class LinkParserTest {
    
    public LinkParserTest() {
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
     * Test of parse method, of class LinkParser.
     */
    @Test
    public void testParse() {
        System.out.println("parse");
        String link = "";
        LinkParser instance = null;
        instance.parse(link);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProgram method, of class LinkParser.
     */
    @Test
    public void testGetProgram() {
        System.out.println("getProgram");
        LinkParser instance = null;
        String expResult = "";
        String result = instance.getProgram();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrderedParams method, of class LinkParser.
     */
    @Test
    public void testGetOrderedParams() {
        System.out.println("getOrderedParams");
        LinkParser instance = null;
        List<String> expResult = null;
        List<String> result = instance.getOrderedParams();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnorderedParams method, of class LinkParser.
     */
    @Test
    public void testGetUnorderedParams() {
        System.out.println("getUnorderedParams");
        LinkParser instance = null;
        Map<String, String> expResult = null;
        Map<String, String> result = instance.getUnorderedParams();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
