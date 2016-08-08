/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(Campaign.class)
public class Campaign_ {
    public static volatile SingularAttribute<Campaign,String> CAMPAIGN_NAME;
    public static volatile SingularAttribute<Campaign,String> CAMPAIGN_GOALS;
}
