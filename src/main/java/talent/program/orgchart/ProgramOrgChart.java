/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import MapAPI.EntityMap;
import java.io.Serializable;
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
public class ProgramOrgChart implements Serializable{
    
    private EntityMap<BusinessUnit,BelongsTo> orgChartMap;
    
    public EntityMap<BusinessUnit, BelongsTo> getOrgChartMap() {
        return orgChartMap;
    }

    public void setOrgChartMap(EntityMap<BusinessUnit, BelongsTo> orgChartMap) {
        this.orgChartMap = orgChartMap;
    }
}
