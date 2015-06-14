package talent.entity.people.potential;


import eds.entity.config.EnterpriseConfiguration_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(PotentialLevel.class)
public class PotentialLevel_ extends EnterpriseConfiguration_ {
    //public static volatile SingularAttribute<PotentialLevel,String> LEVEL_LABEL;
    public static volatile SingularAttribute<PotentialLevel,String> LEVEL_NAME;
    public static volatile SingularAttribute<PotentialLevel,Integer> VALUE;
}
