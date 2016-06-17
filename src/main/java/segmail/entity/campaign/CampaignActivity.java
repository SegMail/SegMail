/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.data.EnterpriseObject;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Can more than 1 Campaign share the same activity???
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CAMPAIGN_ACTIVITY")
public class CampaignActivity extends EnterpriseObject {
    
    private String ACTIVITY_NAME;
           
    private String ACTIVITY_TYPE;
    
    private String ACTIVITY_GOALS;
    
    private String ACTIVITY_CONTENT;
    
    private String STATUS;
    
    private java.sql.Timestamp SCHEDULED_TIME;
    private java.sql.Timestamp START_TIME;
    private java.sql.Timestamp END_TIME;
    private java.sql.Timestamp CANCEL_TIME;
    
    

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
    
    public static void main(String[] args) throws JAXBException {
        XMLEncoder encoder =
           new XMLEncoder(
              new BufferedOutputStream(
                System.out));
        CampaignActivity ca = new CampaignActivity();
        ca.setACTIVITY_CONTENT("fingirng");
        encoder.writeObject(ca);
        encoder.close();
        
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<java version=\"1.8.0_20\" class=\"java.beans.XMLDecoder\">\n" +
" <object class=\"segmail.entity.campaign.CampaignActivity\">\n" +
"  <void property=\"ACTIVITY_CONTENT\">\n" +
"   <string>fingirng</string>\n" +
"  </void>\n" +
" </object>\n" +
"</java>";
         XMLDecoder decoder =
            new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
         CampaignActivity ca2 = (CampaignActivity) decoder.readObject();
         decoder.close();
         System.out.println(ca2.ACTIVITY_CONTENT);
    }
}
