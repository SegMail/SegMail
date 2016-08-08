/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.file;

import eds.entity.transaction.EnterpriseTransaction;
import eds.entity.transaction.EnterpriseTransaction_;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(FileTransaction.class)
public class FileTransaction_ extends EnterpriseTransaction_ {
    
    public static volatile SingularAttribute<FileTransaction,String> NAME;
    public static volatile SingularAttribute<FileTransaction,String> CHECKSUM;
    public static volatile SingularAttribute<FileTransaction,Integer> LAST_PROCESSING_POSITION;
    public static volatile SingularAttribute<FileTransaction,String> LOCATION;
}
