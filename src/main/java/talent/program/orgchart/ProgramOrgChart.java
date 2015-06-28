/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import MapAPI.EntityMap;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import talent.entity.organization.BelongsTo;
import talent.entity.organization.BusinessUnit;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
@Named("ProgramOrgChart")
public class ProgramOrgChart {
    
    private EntityMap<BusinessUnit,BelongsTo> orgChartMap;
    
    private String HSDirectory;

    public EntityMap<BusinessUnit, BelongsTo> getOrgChartMap() {
        return orgChartMap;
    }

    public void setOrgChartMap(EntityMap<BusinessUnit, BelongsTo> orgChartMap) {
        this.orgChartMap = orgChartMap;
    }

    public String getHSDirectory() {
        return HSDirectory;
    }

    public void setHSDirectory(String HSDirectory) {
        this.HSDirectory = HSDirectory;
    }
    
    
}
