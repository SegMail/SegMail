/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.link;

import eds.component.GenericObjectService;
import eds.component.user.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * This was previously implemented in SegURL to resolve the program names in the 
 * request URL. As the Bootstrapping of SegERP becomes more and more complex,
 * we need a more robust service to resolve not just program names but also parameters.
 * <br>
 * The LinkService is designed based on the following assumptions:
 * 1) There is only 1 Program being accessed at any point of time by each end-User.
 * 2) 
 * 
 * @author LeeKiatHaw
 */
@Stateless
public class LinkService {
    
    @EJB private UserService userService;
    @EJB private GenericObjectService objectService;
    
    public static final String PATH_DELIMITER = "/";
    
    private List<String> excludes = new ArrayList<String>();
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String encodeLink(String programName, String command, Map<String,Object> params){
        
        return "";
    }
    
    /**
     * 
     * @param pathInfo
     * @return 
     */
    public String resolveProgramName(String pathInfo){
        String[] pathInfoComp = splitAndCleanPathInfo(pathInfo);
        
        //Get the last element
        //Right now we don't know if there will be more than 1 parameter in pathinfo
        String programName = (pathInfoComp != null && pathInfoComp.length > 0) ? 
                pathInfoComp[pathInfoComp.length-1] : 
                "";
        return (!containsFile(programName)) ? programName : "";
    }
    
    public boolean containsFile(String pathInfo){
        String[] pathInfoComp = splitAndCleanPathInfo(pathInfo);
        String lastComp = (pathInfoComp.length > 0) ? pathInfoComp[pathInfoComp.length-1] : "";
        //It is a file if it contains a . and the position of . is not at the last position
        if(lastComp.contains(".") 
                && lastComp.indexOf(".") < lastComp.length() - 1
                && !containsExcludes(lastComp)){
            return true;
        }
        return false;
            
    }
    
    public boolean containsExcludes(String pathInfo){
        for(String exclude : excludes){
            if(pathInfo.contains(exclude)) //can be contains() too
                return true;
        }
        return false;
    }
    
    private String[] splitAndCleanPathInfo(String pathInfo){
        //If pathInfo is null, just return an empty String array
        if(pathInfo == null) return new String[]{};
        
        String[] allComp = pathInfo.split(PATH_DELIMITER);
        String[] finalAllComp = new String[allComp.length];
        for(int i=0; i<allComp.length; i++){
            finalAllComp[i] = allComp[i].trim();
        }
        return finalAllComp;
    }
}
