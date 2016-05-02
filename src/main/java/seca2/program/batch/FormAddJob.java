/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchProcesingException;
import eds.component.batch.BatchSchedulingService;
import eds.component.data.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import seca2.entity.landing.ServerInstance;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormAddJob")
public class FormAddJob {
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService schedulingService;
    
    //Form data
    private String serviceName;
    private String serviceMethod;
    private long serverId;
    private Date scheduledDateTime;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            program.loadServers();
        }
    }
    
    public void addBatchJob(){
        try {
            DateTime dt = null;
            if(dt != null)
                dt = new DateTime(scheduledDateTime);
            schedulingService.createJobStep(serviceName, serviceMethod, null, -1, serverId,dt);
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Batch job added!", "");
            program.refresh();
        } catch (BatchProcesingException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
    
    public List<ServerInstance> getServers() {
        return program.getServers();
    }
    
    public void setServers(List<ServerInstance> servers) {
        program.setServers(servers);
    }

    public Date getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(Date scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    
    
}
