/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.people.performance;

import eds.entity.config.EnterpriseConfiguration;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="PERFORMANCE_LEVEL")
public class PerformanceLevel extends EnterpriseConfiguration{
    
    private String LEVEL_LABEL;
    private String LEVEL_NAME;
    private int RATING;

    public String getLEVEL_LABEL() {
        return LEVEL_LABEL;
    }

    public void setLEVEL_LABEL(String LEVEL_LABEL) {
        this.LEVEL_LABEL = LEVEL_LABEL;
    }

    public int getRATING() {
        return RATING;
    }

    public void setRATING(int RATING) {
        this.RATING = RATING;
    }

    public String getLEVEL_NAME() {
        return LEVEL_NAME;
    }

    public void setLEVEL_NAME(String LEVEL_NAME) {
        this.LEVEL_NAME = LEVEL_NAME;
    }
    
    
}
