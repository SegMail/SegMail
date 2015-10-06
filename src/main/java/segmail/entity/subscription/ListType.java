/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.config.EnterpriseConfiguration;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="LIST_TYPE")
public class ListType extends EnterpriseConfiguration {
    
    public static enum TYPE{
        REMOTE("REMOTE"),
        LOCAL("LOCAL");
        
        public final String name;
        
        TYPE(String name){
            this.name = name;
        }
    }
}
