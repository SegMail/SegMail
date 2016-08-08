/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign.webservice;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSCampaignActivityMailMerge")
public class WSCampaignActivityMailMerge {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String getTestMMLink(@WebParam(name = "mailmergeCode") String mailmergeCode) {
        
        return "";
    }
}
