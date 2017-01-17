/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign.webservice.rest;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.entity.campaign.CampaignActivity;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
@Path("segmail.entity.campaign.campaignactivity")
@RestSecured
public class CampaignActivityFacadeREST extends AbstractFacade<CampaignActivity> {
    @PersistenceContext(unitName = "HIBERNATE")
    private EntityManager em;

    public CampaignActivityFacadeREST() {
        super(CampaignActivity.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(CampaignActivity entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Long id, CampaignActivity entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public CampaignActivity find(@PathParam("id") Long id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<CampaignActivity> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CampaignActivity> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
        
        /**
         * Can't use this because of java.lang.NoClassDefFoundError: Could not initialize class org.eclipse.persistence.jaxb.BeanValidationHelper
         * The issue appears to be in the packaged EclipseLink in Glassfish: https://bugs.eclipse.org/bugs/show_bug.cgi?id=462322
         * To resolve this now requires a manual setup in production GF. Not worth the extra step and complexity.
         */
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
