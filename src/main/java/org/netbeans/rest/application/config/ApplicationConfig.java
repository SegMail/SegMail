/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author LeeKiatHaw
 */
@javax.ws.rs.ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(org.glassfish.json.jaxrs.JsonStructureBodyReader.class);
        resources.add(org.glassfish.json.jaxrs.JsonStructureBodyWriter.class);
        resources.add(seca2.bootstrap.module.Webservice.REST.RestAuthenticationFilter.class);
        resources.add(seca2.bootstrap.module.Webservice.REST.client.RestClientAuthOutboundFilter.class);
        resources.add(seca2.bootstrap.module.Webservice.REST.server.RestServerAuthEndpoint.class);
        resources.add(segmail.component.account.ClientAccountService.class);
        resources.add(segmail.program.autoresponder.webservice.WSHttpAutoresponder.class);
        resources.add(segmail.program.campaign.webservice.rest.CampaignActivityFacadeREST.class);
        resources.add(segmail.program.campaign.webservice.rest.EnterpriseObjectFacadeREST.class);
        resources.add(segmail.program.dashboard.WSRDashboard.class);
        resources.add(segmail.program.subscribe.subscribe.webservice.WSHttpSubscribe.class);
        resources.add(segmail.program.subscribers.WSRSubscriber.class);
        resources.add(segmail.program.wizard.WSRSetupWizard.class);
    }
    
}
