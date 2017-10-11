/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign.filter;

import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.Table;
import segmail.entity.campaign.CampaignActivity;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CAMPAIGN_ACTIVITY_FILTER")
public class CampaignActivityFilter extends EnterpriseData<CampaignActivity>{
    
    private String FIELD_DISPLAY;
    private String FIELD_KEY;
    private String OPERATOR;
    private String VALUE;

    public String getFIELD_DISPLAY() {
        return FIELD_DISPLAY;
    }

    public void setFIELD_DISPLAY(String FIELD_DISPLAY) {
        this.FIELD_DISPLAY = FIELD_DISPLAY;
    }

    public String getFIELD_KEY() {
        return FIELD_KEY;
    }

    public void setFIELD_KEY(String FIELD_KEY) {
        this.FIELD_KEY = FIELD_KEY;
    }

    public String getOPERATOR() {
        return OPERATOR;
    }

    public void setOPERATOR(String OPERATOR) {
        this.OPERATOR = OPERATOR;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String HTMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
