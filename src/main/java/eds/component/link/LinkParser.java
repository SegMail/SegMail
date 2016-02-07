/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.link;

import java.util.List;
import java.util.Map;

/**
 * This is the Adaptor class for a SegERP internal link. 
 * <br>
 * An example of a link would be /domain/contextpath/program/param1/param2?arg3=3&arg4=5678
 * 
 * 
 * 
 * @author LeeKiatHaw
 */
public final class LinkParser {
    
    /**
     * 
     */
    private String program;
    
    /**
     * 
     */
    private List<String> orderedParams;
    
    /**
     * 
     */
    private Map<String,String> unorderedParams;

    /*public LinkParser() {
        this("");
    }*/
    
    public LinkParser(String link) {
        this.parse(link);
    }
    
    
    /**
     * 
     * @param link 
     */
    public void parse(String link){
        
    }

    public String getProgram() {
        return program;
    }

    public List<String> getOrderedParams() {
        return orderedParams;
    }

    public Map<String, String> getUnorderedParams() {
        return unorderedParams;
    }
    
    /**
     * Checks if the link is for a file resource
     * @return 
     */
    public boolean containsFileResource(){
        throw new UnsupportedOperationException();
    }
}
