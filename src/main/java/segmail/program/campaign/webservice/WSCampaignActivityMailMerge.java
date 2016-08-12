/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign.webservice;

import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSCampaignActivityMailMerge")
public class WSCampaignActivityMailMerge {

    @EJB LandingService landingService;
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "getWebserverAddress")
    public String getWebserverAddress() throws IncompleteDataException {
        ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.WEB);
        
        if(server == null)
            throw new IncompleteDataException("Please contact app administrator to set a landing server.");
        
        return server.getURI();
    }
    
    /**
     * 
     * @param label
     * @return
     * @throws DataValidationException 
     */
    @WebMethod(operationName = "getTestLink")
    public String getTestLink(@WebParam(name = "label") String label) throws DataValidationException, IncompleteDataException {
        MAILMERGE_REQUEST request = MAILMERGE_REQUEST.getByLabel(label);
            if(request == null)
            throw new DataValidationException("Invalid label");
        
        String testServerAddress = this.getWebserverAddress();
        if(testServerAddress == null || testServerAddress.isEmpty())
            throw new IncompleteDataException("Test server is not setup properly. Please contact your system admin.");
        
        if(!testServerAddress.endsWith("/"))
            testServerAddress = testServerAddress + "/";
        
        String name = request.name().toLowerCase();
        
        String testLink = testServerAddress + name + "/" + label;
        
        JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
        resultObjectBuilder.add("name", name);
        resultObjectBuilder.add("testLink", testLink);
        
        String result = resultObjectBuilder.build().toString();
        
        return result;
        
    }
}
