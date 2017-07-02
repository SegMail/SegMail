/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.data.EnterpriseObject;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Can more than 1 Campaign share the same activity???
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CAMPAIGN_ACTIVITY")
@XmlRootElement
public class CampaignActivity extends EnterpriseObject {
    
    private String ACTIVITY_NAME;
           
    private String ACTIVITY_TYPE;
    
    private String ACTIVITY_GOALS;
    
    private String ACTIVITY_CONTENT;
    
    private String ACTIVITY_CONTENT_PREVIEW;
    
    private String STATUS;
    
    private java.sql.Timestamp SCHEDULED_TIME;
    private java.sql.Timestamp START_TIME;
    private java.sql.Timestamp END_TIME;
    private java.sql.Timestamp CANCEL_TIME;
    
    private int LAST_INDEX;

    public String getACTIVITY_TYPE() {
        return ACTIVITY_TYPE;
    }

    public void setACTIVITY_TYPE(String ACTIVITY_TYPE) {
        this.ACTIVITY_TYPE = ACTIVITY_TYPE;
    }

    public String getACTIVITY_NAME() {
        return ACTIVITY_NAME;
    }

    public void setACTIVITY_NAME(String ACTIVITY_NAME) {
        this.ACTIVITY_NAME = ACTIVITY_NAME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public Timestamp getSCHEDULED_TIME() {
        return SCHEDULED_TIME;
    }

    public void setSCHEDULED_TIME(Timestamp SCHEDULED_TIME) {
        this.SCHEDULED_TIME = SCHEDULED_TIME;
    }

    public Timestamp getSTART_TIME() {
        return START_TIME;
    }

    public void setSTART_TIME(Timestamp START_TIME) {
        this.START_TIME = START_TIME;
    }

    public Timestamp getEND_TIME() {
        return END_TIME;
    }

    public void setEND_TIME(Timestamp END_TIME) {
        this.END_TIME = END_TIME;
    }

    public Timestamp getCANCEL_TIME() {
        return CANCEL_TIME;
    }

    public void setCANCEL_TIME(Timestamp CANCEL_TIME) {
        this.CANCEL_TIME = CANCEL_TIME;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getACTIVITY_GOALS() {
        return ACTIVITY_GOALS;
    }

    public void setACTIVITY_GOALS(String ACTIVITY_GOALS) {
        this.ACTIVITY_GOALS = ACTIVITY_GOALS;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getACTIVITY_CONTENT() {
        return ACTIVITY_CONTENT;
    }

    public void setACTIVITY_CONTENT(String ACTIVITY_CONTENT) {
        this.ACTIVITY_CONTENT = ACTIVITY_CONTENT;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getACTIVITY_CONTENT_PREVIEW() {
        return ACTIVITY_CONTENT_PREVIEW;
    }

    public void setACTIVITY_CONTENT_PREVIEW(String ACTIVITY_CONTENT_PREVIEW) {
        this.ACTIVITY_CONTENT_PREVIEW = ACTIVITY_CONTENT_PREVIEW;
    }

    public int getLAST_INDEX() {
        return LAST_INDEX;
    }

    public void setLAST_INDEX(int LAST_INDEX) {
        this.LAST_INDEX = LAST_INDEX;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String alias() {
        return getACTIVITY_NAME();
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
