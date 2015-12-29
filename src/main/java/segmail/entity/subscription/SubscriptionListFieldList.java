/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST_FIELD_LIST")
public class SubscriptionListFieldList extends EnterpriseData<SubscriptionList> {
    
    private List<SubscriptionListField> FIELD_LIST = new ArrayList<SubscriptionListField>();

    //Collections are lazily-loaded by default: http://stackoverflow.com/a/11746720
    //Must set mappedBy, else an extra table will be generated: http://stackoverflow.com/a/5165829
    @OneToMany(
            cascade=CascadeType.ALL, 
            fetch = FetchType.EAGER, 
            mappedBy="LIST",
            orphanRemoval = true) //Doesn't seem to work, remove constraint?
    public List<SubscriptionListField> getFIELD_LIST() {
        return FIELD_LIST;
    }

    public void setFIELD_LIST(List<SubscriptionListField> FIELD_LIST) {
        this.FIELD_LIST = FIELD_LIST;
    }
    
    public void addField(SubscriptionListField newField){
        if(this.FIELD_LIST == null)
            this.FIELD_LIST = new ArrayList<SubscriptionListField>();
        
        this.FIELD_LIST.add(newField);
        newField.setLIST(this); //Bidrectional!
    }
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
