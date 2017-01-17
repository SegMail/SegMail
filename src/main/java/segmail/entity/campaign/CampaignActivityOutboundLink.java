/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.component.encryption.EncryptionType;
import eds.component.encryption.EncryptionUtility;
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
    
    private String ORIGINAL_LINK_HTML;
    
    private boolean ACTIVE;

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

    public String getORIGINAL_LINK_HTML() {
        return ORIGINAL_LINK_HTML;
    }

    public void setORIGINAL_LINK_HTML(String ORIGINAL_LINK_HTML) {
        this.ORIGINAL_LINK_HTML = ORIGINAL_LINK_HTML;
    }

    public boolean isACTIVE() {
        return ACTIVE;
    }

    public void setACTIVE(boolean ACTIVE) {
        this.ACTIVE = ACTIVE;
    }
    
    @Override
    public void randInit() {
        
    }

    @Override
    public Object generateKey() {
        String hash = EncryptionUtility.getHash(getOWNER().toString()+getSNO()+getLINK_TEXT()+getLINK_TARGET(), EncryptionType.SHA256);
        
        return hash;
    }

    @Override
    public String HTMLName() {
        return super.OWNER.getOBJECT_NAME();
    }
    
    public String constructLink() {
        return "<a target='_blank' href='"+this.LINK_TARGET+"' >"+this.LINK_TEXT+"</a>";
    }
}
