/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This is the Adaptor class for a SegERP logical path. 
 * <br>
 * An example of a link would be /program/param1/param2.
 * <br>
 * A SegERP link is derived from the pathInfo of a ServletRequest. This means that
 * it has no request query string like a Servlet URL pattern. The first element 
 * is always the program name, followed by the rest of the parameters.
 * <br>
 * Some properties of a logical path:
 * <ol>
 * <li>It is a program name followed by its parameters, delimited by forward slash "/"</li>
 * <li>The presence of leading slashes and trailing slashes are not mandatory. 
 * "/program/param1/param2/" and "program/param1/param2" should both give the same program name
 * and parameters.</li>
 * <li>It contains a file if any of its elements contains a string that is separated by 
 * a ".". This file element consist of a filename and an extension, which should only
 * contain numbers and letters. "file.n!" and "file.!n" are not valid filenames.</li>
 * 
 * </ol>
 * 
 * 
 * @author LeeKiatHaw
 */
public final class LogicalPathParser {
    
    private final String originalLink;
    
    /**
     * 
     */
    private String program = "";
    
    /**
     * An ordered list of parameters that appears in the link. 
     */
    private List<String> orderedParams;
    
    /**
     * The viewId is the filename of the JSF xhtml file. It should only end with 
     * .xhtml and not any other extensions at this point. Do not recognize any 
     * paths that has only this as a file resource.
     */
    private final String viewId;
    
    /**
     * Additional filtering because sometimes your URL will contain no servletPath
     * and your servletPath will contain your entire desired pathInfo. Eg.
     * "/SegMail/subscribe" will give servletPath = "/subscribe" and pathInfo = ""
     * "/SegMail/program/subscribe" will give servletPath = "/program" and pathInfo = "/subscribe"
     * both should point to the same view.
     */
    private final String servletPath;
    
    
    public LogicalPathParser(String link, String viewId, String servletPath) {
        this.originalLink = link;
        this.viewId = (viewId == null) ? "" : viewId;
        this.servletPath = (servletPath == null) ? "" : servletPath;
        this.parse(link);
        
    }
    
    /**
     * Helper method to intialize all variables.
     * 
     * @param link 
     */
    private void parse(String link){
        
        //Before splitting, we need to remove the servletPath
        String linkNoServlet = (link.startsWith(servletPath)) ? link.replaceFirst(servletPath, "") : link;
        
        String[] splitElements = linkNoServlet.split("/");
        
        //program = (splitElements.length > 0) ? splitElements[0] : "";
        
        orderedParams = new ArrayList<String>();
        for(int i=0; i<splitElements.length; i++){
            if((program == null || program.isEmpty()) 
                    && splitElements[i] != null 
                    && !splitElements[i].isEmpty()){ //program should be set first
                program = splitElements[i];
                continue;
            }
            if(program != null && !program.isEmpty()) //Program has been set
                orderedParams.add(splitElements[i]);
        }
        
        //Additional step to hide serlvetPath
        /*
        if(program!= null && program.equalsIgnoreCase(servletPath)){
            String actualProgram = (orderedParams.isEmpty()) ? "" : orderedParams.remove(0);
            program = actualProgram;
        }*/
    }

    public String getProgram() {
        return program;
    }

    public List<String> getOrderedParams() {
        return orderedParams;
    }
    
    /**
     * Checks if the link is for a file resource. A file resource is any link
     * that contains an element that is separated by a "." with both a name and 
     * an extension. "a.b" is a file but ".ab" and "ab." are not files.
     * @return 
     */
    public boolean containsFileResource(){
        //Check if program is a file resource
        
        if(!program.equals(viewId) && elementContainsFile(program)){
            return true;
        }
        
        //Check if any of the parameters is a file resource
        for(String element : orderedParams){
            if(!element.equals(viewId) && elementContainsFile(element))
                return true;
        }
        
        //Check in servletPath, in case the file exists in servletPath
        if(elementContainsFile(servletPath))
            return true;
        
        return false;
    }
    
    /**
     * An element is a file if all its sub-elements contain letters. If one of it
     * doesn't then it is not a file.
     * 
     * @param element
     * @return 
     */
    private boolean elementContainsFile(String element){
        String[] splitElement = element.split("\\.");
        boolean isFile = (splitElement.length > 1); //Assume it is a file first
        if(!isFile)
            return false;
        
        for(int i=0; i<splitElement.length; i++){
            String programElement = splitElement[i];
            if(!programElement.matches(".*[a-zA-Z]+.*")) //If any of its elements doesn't contain a letter, it is not a file
                return false;
        }
        
        //If the last element, which is the file extension, doesn't start with a letter, then it is not a file
        if(!Character.isAlphabetic(splitElement[splitElement.length-1].charAt(0))) 
            return false;
        
        //If the last element, which is the file extension, contains a special character, then it is not a file
        if(!splitElement[splitElement.length-1].matches("[a-zA-Z0-9]*"))
            return false;
        
        return isFile;
    }
    
    /**
     * Return the link that was parsed with leading and trailing slash by 
     * default.
     * 
     * @return 
     */
    public String getLink(){
        String link = "/".concat(getProgram());
        for(String param : orderedParams){
            link = link.concat("/").concat(param);
        }
        if(link.charAt(link.length()-1) != '/') link = link.concat("/");
        
        return link;
    }
    
    public String getViewId(){
        return this.viewId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.program);
        hash = 89 * hash + Objects.hashCode(this.orderedParams);
        hash = 89 * hash + Objects.hashCode(this.viewId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogicalPathParser other = (LogicalPathParser) obj;
        if (!Objects.equals(this.program, other.program)) {
            return false;
        }
        if (!Objects.equals(this.orderedParams, other.orderedParams)) {
            return false;
        }
        if (!Objects.equals(this.viewId, other.viewId)) {
            return false;
        }
        return true;
    }
    
    
}
