/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.people.potential;

import eds.entity.config.EnterpriseConfiguration;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="POTENTIAL_LEVEL")
public class PotentialLevel extends EnterpriseConfiguration {
    
    private String LEVEL_LABEL;
    private String LEVEL_NAME;
    private int WEIGHT;

    public String getLEVEL_LABEL() {
        return LEVEL_LABEL;
    }

    public void setLEVEL_LABEL(String LEVEL_LABEL) {
        this.LEVEL_LABEL = LEVEL_LABEL;
    }

    public String getLEVEL_NAME() {
        return LEVEL_NAME;
    }

    public void setLEVEL_NAME(String LEVEL_NAME) {
        this.LEVEL_NAME = LEVEL_NAME;
    }

    public int getWEIGHT() {
        return WEIGHT;
    }

    public void setWEIGHT(int WEIGHT) {
        this.WEIGHT = WEIGHT;
    }
    
    
}
