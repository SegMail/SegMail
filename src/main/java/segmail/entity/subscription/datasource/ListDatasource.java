/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

import eds.entity.data.EnterpriseData;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST_DATASOURCE")
@EntityListeners({
    ListDatasourceListener.class
})
public class ListDatasource extends EnterpriseData<SubscriptionList> {
    
    private String NAME;
    
    private String ENDPOINT_TYPE;
    
    private String SERVER_NAME;
    
    private String DB_NAME;
    
    private String DESCRIPTION;
    
    private int PORT;
    
    private String USERNAME;
    
    private String PASSWORD;
    
    private boolean ACTIVE;
    
    /**
     * Flag to determine if a status field is to be used for this list.
     * 
     * A status field is a DB column at the client's DB to indicate the status of 
     * the subscriber - eg. if they are still subscribed, bounced or removed.
     * If no status fields are provided, then it will be assumed that if a record
     * is found, the subscriber is subscribed and if no record is found, the subscriber
     * is removed.
     */
    private boolean USE_STATUS_FIELD;
    
    /**
     * The field at the remote DB that will reflect the status of the subscriber.
     */
    private String STATUS_FIELD;
    
    /**
     * The value of the status field that reflects an active subscription status.
     */
    private String STATUS_FIELD_VALUE;
    
    /**
     * For databases
     * 
     */
    private String TABLE_NAME;
    
    /**
     * This is the name of the field that will be used as an identifier, which is
     * usually the email address. 
     * 
     */
    private String KEY_FIELD;
    
    /**
     * The last sync time.
     */
    private java.sql.Timestamp LAST_SYNC;
    
    private String LAST_SYNC_MESSAGE;
    
    /**
     * The starting index of the next sync that will be performed. This is to 
     * improve performance so that each sync don't start from 0. Once a single
     * cycle of the sync has completed, this index should be reset to 0.
     */
    private int NEXT_SYNC_NEW_INDEX;
    
    private int NEXT_SYNC_REMOVE_INDEX;
    
    /**
     * This represents the number of times the entire list has been run through
     * for syncing newly-added subscribers.
     * 
     */
    private int SYNC_NEW_CYCLES;
    
    /**
     * This represents the number of times the entire list has been run through
     * for syncing removed subscribers.
     */
    private int SYNC_REMOVE_CYCLES;
    

    public ListDatasource() {
        //Default values 
        this.setENDPOINT_TYPE(DATASOURCE_ENDPOINT_TYPE.defaultValue().name());
    }

    public ListDatasource(String NAME, String ENDPOINT_TYPE, String SERVER_NAME, String DB_NAME, String DESCRIPTION, String USERNAME, String PASSWORD, String TABLE_NAME, String KEY_FIELD) {
        this.NAME = NAME;
        this.ENDPOINT_TYPE = ENDPOINT_TYPE;
        this.SERVER_NAME = SERVER_NAME;
        this.DB_NAME = DB_NAME;
        this.DESCRIPTION = DESCRIPTION;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.TABLE_NAME = TABLE_NAME;
        this.KEY_FIELD = KEY_FIELD;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getENDPOINT_TYPE() {
        return ENDPOINT_TYPE;
    }

    public void setENDPOINT_TYPE(String ENDPOINT_TYPE) {
        this.ENDPOINT_TYPE = ENDPOINT_TYPE;
    }
    
    public void setENDPOINT_TYPE(DATASOURCE_ENDPOINT_TYPE ENDPOINT_TYPE) {
        this.ENDPOINT_TYPE = ENDPOINT_TYPE.name;
    }

    public String getSERVER_NAME() {
        return SERVER_NAME;
    }

    public void setSERVER_NAME(String SERVER_NAME) {
        this.SERVER_NAME = SERVER_NAME;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public boolean isACTIVE() {
        return ACTIVE;
    }

    public void setACTIVE(boolean ACTIVE) {
        this.ACTIVE = ACTIVE;
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    public void setDB_NAME(String DB_NAME) {
        this.DB_NAME = DB_NAME;
    }

    public String getKEY_FIELD() {
        return KEY_FIELD;
    }

    public void setKEY_FIELD(String KEY_FIELD) {
        this.KEY_FIELD = KEY_FIELD;
    }

    public String getSTATUS_FIELD() {
        return STATUS_FIELD;
    }

    public boolean isUSE_STATUS_FIELD() {
        return USE_STATUS_FIELD;
    }

    public void setUSE_STATUS_FIELD(boolean USE_STATUS_FIELD) {
        this.USE_STATUS_FIELD = USE_STATUS_FIELD;
    }

    public void setSTATUS_FIELD(String STATUS_FIELD) {
        this.STATUS_FIELD = STATUS_FIELD;
    }

    public Timestamp getLAST_SYNC() {
        return LAST_SYNC;
    }

    public void setLAST_SYNC(Timestamp LAST_SYNC) {
        this.LAST_SYNC = LAST_SYNC;
    }

    public String getSTATUS_FIELD_VALUE() {
        return STATUS_FIELD_VALUE;
    }

    public void setSTATUS_FIELD_VALUE(String STATUS_FIELD_VALUE) {
        this.STATUS_FIELD_VALUE = STATUS_FIELD_VALUE;
    }

    public int getNEXT_SYNC_NEW_INDEX() {
        return NEXT_SYNC_NEW_INDEX;
    }

    public void setNEXT_SYNC_NEW_INDEX(int NEXT_SYNC_NEW_INDEX) {
        this.NEXT_SYNC_NEW_INDEX = NEXT_SYNC_NEW_INDEX;
    }

    public int getSYNC_NEW_CYCLES() {
        return SYNC_NEW_CYCLES;
    }

    public void setSYNC_NEW_CYCLES(int SYNC_NEW_CYCLES) {
        this.SYNC_NEW_CYCLES = SYNC_NEW_CYCLES;
    }

    public int getSYNC_REMOVE_CYCLES() {
        return SYNC_REMOVE_CYCLES;
    }

    public void setSYNC_REMOVE_CYCLES(int SYNC_REMOVE_CYCLES) {
        this.SYNC_REMOVE_CYCLES = SYNC_REMOVE_CYCLES;
    }

    public String getLAST_SYNC_MESSAGE() {
        return LAST_SYNC_MESSAGE;
    }

    public void setLAST_SYNC_MESSAGE(String LAST_SYNC_MESSAGE) {
        this.LAST_SYNC_MESSAGE = LAST_SYNC_MESSAGE;
    }
    
    public void setLAST_SYNC_MESSAGE(LAST_SYNC_RESULT message) {
        this.LAST_SYNC_MESSAGE = message.label;
    }

    public int getNEXT_SYNC_REMOVE_INDEX() {
        return NEXT_SYNC_REMOVE_INDEX;
    }

    public void setNEXT_SYNC_REMOVE_INDEX(int NEXT_SYNC_REMOVE_INDEX) {
        this.NEXT_SYNC_REMOVE_INDEX = NEXT_SYNC_REMOVE_INDEX;
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
    
    /**
     * This is written for the purpose of checking when to re-query the remote 
     * DB columns in FormListDatasource.
     * 
     * @return a concatenation of all the connection fields
     */
    public String connectionKey() {
        return 
                this.getDB_NAME() +
                this.getENDPOINT_TYPE() +
                this.getPASSWORD() + 
                this.getSERVER_NAME() +
                this.getTABLE_NAME() + 
                this.getUSERNAME();
    }
}
