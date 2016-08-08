/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign.webservice.client;

import eds.component.data.EntityNotFoundException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import seca2.bootstrap.module.Webservice.client.GenericWSInterface;

/**
 * This is exposed to the public, so only public methods should be available.
 * 
 * @author LeeKiatHaw
 */
@WebService(
        name = "WSCampaignActivityLink", 
        targetNamespace = "http://webservice.campaign.program.segmail/")
public interface WSCampaignActivityLinkInterface extends GenericWSInterface {
    
    @WebMethod(operationName = "redirectAndUpdate")
    public String redirectAndUpdate(
            @WebParam(name = "linkKey") String linkKey ) throws EntityNotFoundException;
}
