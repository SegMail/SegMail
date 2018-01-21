/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

import eds.entity.data.EnterpriseData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionList;

/**
 * It is assumed now that each list can only have 1 datasource, and therefore, 
 * 1 set of data mapping.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST_DATA_MAPPING")
@EntityListeners({
    ListDataMappingListener.class
})
public class ListDataMapping extends EnterpriseData<SubscriptionList>{
    
    /**
     * This should match SubscriptionListField.generateKey()
     */
    private String KEY_NAME;
    
    /**
     * This should match SubscriptionListField.FIELD_NAME
     */
    private String LOCAL_NAME;
    
    /**
     * This should match the reference name in the external app
     */
    private String FOREIGN_NAME;
    
    /**
     * Same field as SubscriptionListField.TYPE that takes on the string value of 
     * enum FIELD_TYPE.
     */
    private String TYPE;

    public ListDataMapping() {
    }

    public ListDataMapping(String KEY_NAME, String LOCAL_NAME, String FOREIGN_NAME, String TYPE) {
        this.KEY_NAME = KEY_NAME;
        this.LOCAL_NAME = LOCAL_NAME;
        this.FOREIGN_NAME = FOREIGN_NAME;
        this.TYPE = TYPE;
    }

    public String getKEY_NAME() {
        return KEY_NAME;
    }

    public void setKEY_NAME(String KEY_NAME) {
        this.KEY_NAME = KEY_NAME;
    }

    public String getLOCAL_NAME() {
        return LOCAL_NAME;
    }

    public void setLOCAL_NAME(String LOCAL_NAME) {
        this.LOCAL_NAME = LOCAL_NAME;
    }

    public String getFOREIGN_NAME() {
        return FOREIGN_NAME;
    }

    public void setFOREIGN_NAME(String FOREIGN_NAME) {
        this.FOREIGN_NAME = FOREIGN_NAME;
    }
    
    public String getTYPE() {
        return TYPE;
    }
    
    public void setTYPE(String type) {
        this.TYPE = type;
    }

    public void setTYPE(FIELD_TYPE TYPE) {
        this.TYPE = TYPE.name;
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
     * This is initially written only for testing purpose, but can be used for 
     * production.
     * 
     * @param map
     * @return 
     */
    public static List<ListDataMapping> initMappingsFromMap(Map<String,String> map) {
        List<ListDataMapping> mappings = new ArrayList<>();
        for(Entry<String,String> entry : map.entrySet()) {
            ListDataMapping mapping = new ListDataMapping();
            mapping.setKEY_NAME(entry.getKey()); //This should be stored as KEY_NAME, not LOCAL_NAME
            mapping.setFOREIGN_NAME(entry.getValue());
            mapping.setTYPE(FIELD_TYPE.TEXT); //Default, does not matter if an arbitrary list of fields are added
            
            mappings.add(mapping);
        }
        
        return mappings;
    }
}
