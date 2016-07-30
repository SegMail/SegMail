/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign.webservice;

import eds.component.data.IncompleteDataException;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.CampaignActivityOutboundLink;
import segmail.program.campaign.ProgramCampaign;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSCampaignActivityLink")
@HandlerChain(file = "handlers-server.xml")
public class WSCampaignActivityLink {

    @EJB CampaignService campService;
    @EJB LandingService landingService;
    
    /**
     * If this is not injected, no service calls can proceed.
     * 
     */
    @Inject ProgramCampaign program;
    /**
     * This is a sample web service operation
     * @param linkTarget
     * @param linkText
     * @param index
     * @return a generated redirect link
     */
    @WebMethod(operationName = "createOrUpdateLink")
    public String createOrUpdateLink(
            @WebParam(name = "linkTarget") String linkTarget, 
            @WebParam(name = "linkText") String linkText, 
            @WebParam(name = "index") int index) throws IncompleteDataException {
        
        if(program == null || program.getEditingCampaignId() <= 0)
            throw new RuntimeException("Program is not set, this service was not called from the correct page.");
        
        //long listId = program.getEditingCampaignId();
        
        CampaignActivityOutboundLink link = campService.createOrUpdateLink(program.getEditingActivity(), linkTarget, linkText, index);
        
        String redirectLink = campService.constructLink(link);
        
        return redirectLink;
    }
}
