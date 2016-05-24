/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.transaction.EnterpriseTransaction_;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(Email.class)
public class Email_ extends EnterpriseTransaction_ {
    public static volatile SingularAttribute<Email,String> SUBJECT;
    public static volatile SingularAttribute<Email,String> BODY;
    public static volatile SingularAttribute<Email,String> SENDER_ADDRESS;
    public static volatile SingularAttribute<Email,String> SENDER_NAME;
    public static volatile SetAttribute<Email,String> RECIPIENTS;
    public static volatile SetAttribute<Email,String> REPLY_TO_ADDRESSES;
    public static volatile SetAttribute<Email,Integer> RETRIES;
}
