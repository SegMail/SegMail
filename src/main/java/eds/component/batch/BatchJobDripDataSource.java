/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.data.DripDatasource;
import eds.component.data.DripFeederService;
import eds.entity.batch.BatchJobRun;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class BatchJobDripDataSource extends DripFeederService<BatchJobRun> implements Serializable {
    
    @EJB BatchSchedulingService batchService;
    
    private Timestamp start;
    private Timestamp end;
    private List<String> statuses = new ArrayList<>();

    
    @Override
    public List<BatchJobRun> refill(int startIndex, int size) {
        return batchService.getBatchRuns(start, end, statuses, startIndex, size);
    }
    
    @Override
    public long countFromDB() {
        if(start == null)
            return 0;
        if(end == null)
            return 0;
        return batchService.countBatchJobRuns(start, end, statuses);
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        if(start.equals(this.start)) //Why is it always null here?
            return;
        this.start = start;
        this.init();
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        if(end.equals(this.end))
            return;
        this.end = end;
        this.init();
    }

    public void addStatus(String status) {
        if(statuses.contains(status))
            return;
        statuses.add(status);
        init();
    }
    
    public void removeStatus(String status) {
        if(!statuses.contains(status))
            return;
        statuses.remove(status);
        init();
    }
    
    public void addStatuses(List<String> statuses) {
        for(String status : statuses) {
            this.addStatus(status);
        }
    }
    
    public void removeStatuses(List<String> statuses) {
        for(String status : statuses) {
            this.removeStatus(status);
        }
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
        this.init();
    }
    
    public boolean compareStatuses(List<String> statuses) {
        Collections.sort(statuses);
        Collections.sort(this.statuses);
        
        return this.statuses.equals(statuses);
    }
}
