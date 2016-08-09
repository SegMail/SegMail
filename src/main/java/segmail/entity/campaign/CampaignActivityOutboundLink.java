/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CAMPAIGN_ACTIVITY_OUTBOUND_LINK")
@EntityListeners({
    CampaignActivityOutboundLinkListener.class
})
public class CampaignActivityOutboundLink extends EnterpriseData<CampaignActivity> {

    private String LINK_KEY;
    
    private String LINK_TARGET;
    
    private String LINK_TEXT;

    public String getLINK_TARGET() {
        return LINK_TARGET;
    }

    public void setLINK_TARGET(String LINK_TARGET) {
        this.LINK_TARGET = LINK_TARGET;
    }

    public String getLINK_KEY() {
        return LINK_KEY;
    }

    public void setLINK_KEY(String LINK_KEY) {
        this.LINK_KEY = LINK_KEY;
    }

    public String getLINK_TEXT() {
        return LINK_TEXT;
    }

    public void setLINK_TEXT(String LINK_TEXT) {
        this.LINK_TEXT = LINK_TEXT;
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
