/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.data.EnterpriseData;
import eds.entity.data.EnterpriseData_;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(VerifiedSendingAddress.class)
public class VerifiedSendingAddress_ extends EnterpriseData_ {
    
    public static volatile SingularAttribute<VerifiedSendingAddress,String> VERIFIED_ADDRESS;
    
}
