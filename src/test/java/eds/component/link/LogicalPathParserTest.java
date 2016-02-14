/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.link;

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
public class LogicalPathParserTest {
    

    final String DEFAULT_VIEW_ID = "index.xhtml";
    
    public LogicalPathParserTest() {
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
     * Test of parse method, of class LogicalPathParser.
     */
    @Test
    public void testGetProgramNameSimple1() {
        String link = "/program/param0/param1/param2/param3/param4/param5";
        int paramSize = 6;
        LogicalPathParser parser = new LogicalPathParser(link,DEFAULT_VIEW_ID);
        
        assertEquals(parser.getProgram(),"program");
        
        List<String> params = parser.getOrderedParams();
        assertEquals(params.size(),paramSize);
        for(int i=0; i<params.size(); i++){
            assertEquals(params.get(i),"param"+i);
        }
    }
    
    @Test
    public void testGetProgramNameEmpty() {
        String link = "/";
        int paramSize = 0;
        LogicalPathParser parser = new LogicalPathParser(link,DEFAULT_VIEW_ID);
        
        assertEquals(parser.getProgram(),"");
        
        List<String> params = parser.getOrderedParams();
        assertEquals(params.size(),paramSize);
    }

    @Test
    public void testPrePostSlashAgnostic() {
        LogicalPathParser linkNoSlash = new LogicalPathParser("program/param1/param2/param3/param4/param5",DEFAULT_VIEW_ID);
        LogicalPathParser linkNoSlashFront = new LogicalPathParser("program/param1/param2/param3/param4/param5/",DEFAULT_VIEW_ID);
        LogicalPathParser linkNoSlashBack = new LogicalPathParser("/program/param1/param2/param3/param4/param5",DEFAULT_VIEW_ID);
        LogicalPathParser slashFrontBack = new LogicalPathParser("/program/param1/param2/param3/param4/param5/",DEFAULT_VIEW_ID);
        System.out.println(linkNoSlash.getLink());
        System.out.println(linkNoSlashFront.getLink());
        System.out.println(linkNoSlashBack.getLink());
        System.out.println(slashFrontBack.getLink());
        
        //Not enough
        assertEquals(linkNoSlash.getLink(),linkNoSlashFront.getLink());
        assertEquals(linkNoSlash.getLink(),linkNoSlashBack.getLink());
        assertEquals(linkNoSlash.getLink(),slashFrontBack.getLink());
        assertEquals(linkNoSlashFront.getLink(),linkNoSlashBack.getLink());
        assertEquals(linkNoSlashFront.getLink(),slashFrontBack.getLink());
        assertEquals(linkNoSlashBack.getLink(),slashFrontBack.getLink());
    }
    
    @Test
    public void testNotFile1() {
        LogicalPathParser notFile1 = new LogicalPathParser("program/.param1/param2./.param3/param4./.param5/.","");
        assertFalse(notFile1.containsFileResource());
    }
    
    @Test
    public void testNotFile2() {
        LogicalPathParser notFile2 = new LogicalPathParser("program/param1/param2./.param3/param4./.param5/program?.//","");
        assertFalse(notFile2.containsFileResource());
    }
    
    @Test
    public void testNotFile3() {
        LogicalPathParser notFile3 = new LogicalPathParser("program/param1/param2./.param3/param4./.param5//e/.file","");
        assertFalse(notFile3.containsFileResource());
    }
    
    @Test
    public void testNotFile4() {
        LogicalPathParser notFile4 = new LogicalPathParser("program/param1/param2./.param3/param4./.param5/program.e!","");
        assertFalse(notFile4.containsFileResource());
    }
    
    @Test
    public void testNotFile5() {
        LogicalPathParser notFile5 = new LogicalPathParser("program/".concat(DEFAULT_VIEW_ID),DEFAULT_VIEW_ID);
        assertFalse(notFile5.containsFileResource());
    }
    
    @Test
    public void testNotFile6() {
        LogicalPathParser notFile6 = new LogicalPathParser("program/".concat(DEFAULT_VIEW_ID).concat("/param/"),DEFAULT_VIEW_ID);
        assertFalse(notFile6.containsFileResource());
    }
    
    @Test
    public void testNotFile7() {
        LogicalPathParser notFile7 = new LogicalPathParser("program/param1/param2./.param3/param.4/.param5/program.e!","");
        assertFalse(notFile7.containsFileResource());
    }
    
    @Test
    public void testNotFile8() {
        LogicalPathParser notFile8 = new LogicalPathParser("program/param1/param2./.param3/param.4/.param5/program.e 1","");
        assertFalse(notFile8.containsFileResource());
    }
    
    @Test
    public void testFile1() {
        LogicalPathParser file1 = new LogicalPathParser("program/p.aram1/param2./.param3/param4./.param5/.",DEFAULT_VIEW_ID);
        assertTrue(file1.containsFileResource());
    }
    
    @Test
    public void testFile2() {
        LogicalPathParser file2 = new LogicalPathParser("program/.param1/param2./.param3/para.m4/.param5/.","");
        assertTrue(file2.containsFileResource());
    }
    
    @Test
    public void testFile3() {
        LogicalPathParser file3 = new LogicalPathParser("/javax.faces.resource/jsf.js",DEFAULT_VIEW_ID);
        assertTrue(file3.containsFileResource());
    }
    
    @Test
    public void testFile4() {
        LogicalPathParser file4 = new LogicalPathParser("program/".concat(DEFAULT_VIEW_ID).concat("/param/"),"");
        assertTrue(file4.containsFileResource());
    }
}
