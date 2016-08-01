/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CAMPAIGN")
public class Campaign extends EnterpriseObject {
    
    protected String CAMPAIGN_NAME;
    
    protected String CAMPAIGN_GOALS;
    
    /**
     * The email displayed in the From field when viewed by the recipient.
     */
    private String OVERRIDE_SEND_AS_EMAIL;
    
    /**
     * The name displayed in the From field when viewed by the recipient.
     */
    private String OVERRIDE_SEND_AS_NAME;

    public String getCAMPAIGN_NAME() {
        return CAMPAIGN_NAME;
    }

    public void setCAMPAIGN_NAME(String CAMPAIGN_NAME) {
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getCAMPAIGN_GOALS() {
        return CAMPAIGN_GOALS;
    }

    public void setCAMPAIGN_GOALS(String CAMPAIGN_GOALS) {
        this.CAMPAIGN_GOALS = CAMPAIGN_GOALS;
    }

    public String getOVERRIDE_SEND_AS_EMAIL() {
        return OVERRIDE_SEND_AS_EMAIL;
    }

    public void setOVERRIDE_SEND_AS_EMAIL(String OVERRIDE_SEND_AS_EMAIL) {
        this.OVERRIDE_SEND_AS_EMAIL = OVERRIDE_SEND_AS_EMAIL;
    }

    public String getOVERRIDE_SEND_AS_NAME() {
        return OVERRIDE_SEND_AS_NAME;
    }

    public void setOVERRIDE_SEND_AS_NAME(String OVERRIDE_SEND_AS_NAME) {
        this.OVERRIDE_SEND_AS_NAME = OVERRIDE_SEND_AS_NAME;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String alias() {
        return CAMPAIGN_NAME;
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
