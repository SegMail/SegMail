/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIBER_COUNT")
public class SubscriberCount extends EnterpriseData<SubscriptionList>{
    
    private Map<String,Long> COUNT = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="STATUS")
    @CollectionTable(
            name="SUBSCRIBER_COUNT_MAP"
            ,joinColumns={
                @JoinColumn(name="OWNER", referencedColumnName="OWNER", 
                        foreignKey = @ForeignKey(name = "OWNER", value = NO_CONSTRAINT))
                ,@JoinColumn(name="START_DATE", referencedColumnName="START_DATE", 
                        foreignKey = @ForeignKey(name = "START_DATE", value = NO_CONSTRAINT))
                ,@JoinColumn(name="END_DATE", referencedColumnName="END_DATE", 
                        foreignKey = @ForeignKey(name = "END_DATE", value = NO_CONSTRAINT))
                ,@JoinColumn(name="SNO", referencedColumnName="SNO", 
                        foreignKey = @ForeignKey(name = "SNO", value = NO_CONSTRAINT))
                ,@JoinColumn(name="DATA_TYPE", referencedColumnName="DATA_TYPE", 
                        foreignKey = @ForeignKey(name = "DATA_TYPE", value = NO_CONSTRAINT))
            }
            ,foreignKey = @ForeignKey(name = "OWNER", value = NO_CONSTRAINT)
    )
    public Map<String, Long> getCOUNT() {
        return COUNT;
    }

    public void setCOUNT(Map<String, Long> COUNT) {
        this.COUNT = COUNT;
    }
    
    public long count(SUBSCRIPTION_STATUS status) {
        if(getCOUNT() == null || getCOUNT().isEmpty())
            return 0;
        
        if(status == null)
            return getCOUNT().values().stream().mapToLong(l->l.longValue()).sum();
        
        return getCOUNT().get(status.name);
    }
    
    public long countActive() {
        long confirmed = this.count(SUBSCRIPTION_STATUS.CONFIRMED);
        long newly = this.count(SUBSCRIPTION_STATUS.NEW);
        
        return confirmed + newly;
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
