/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BatchJob;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormJobList")
public class FormJobList {
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService scheduleService;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            
        }
    }
    
    public List<BatchJob> getBatchJobs() {
        return program.getBatchJobs();
    }

    public void setBatchJobs(List<BatchJob> batchJobs) {
        program.setBatchJobs(batchJobs);
    }
    
    public Map<String, String> getBatchJobStatusMapping() {
        return program.getBatchJobStatusMapping();
    }

    public void setBatchJobStatusMapping(Map<String, String> batchJobStatusMapping) {
        program.setBatchJobStatusMapping(batchJobStatusMapping);
    }
    
    public void loadBatchJobs(){
        
    }
}
