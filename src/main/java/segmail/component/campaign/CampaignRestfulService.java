/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.link.CampaignActivityOutboundLink;
import segmail.entity.campaign.link.CampaignLinkClick;
import segmail.entity.campaign.link.CampaignLinkClick_;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
@Path("campaign")
public class CampaignRestfulService {
    
    @EJB
    CampaignService campService;
    
    
    @Path("conversion/{campaignActivityId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RestSecured
    public double ConversionRate(@PathParam("campaignActivityId") long campaignActivityId) {
        long totalTargeted = campService.countTargetedSubscribersForActivity(campaignActivityId);
        long totalConverted = campService.countConvertedEmails(campaignActivityId);
        
        return totalConverted/totalTargeted;
        
    }
}
