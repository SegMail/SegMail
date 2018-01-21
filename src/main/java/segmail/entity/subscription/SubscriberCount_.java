/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData_;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(SubscriberCount.class)
public class SubscriberCount_ extends EnterpriseData_ {
    public static volatile MapAttribute<SubscriberCount,String,Integer> COUNT_MAP;
    
}
