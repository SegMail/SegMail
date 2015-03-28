/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.file;

import javax.persistence.metamodel.SingularAttribute;
import eds.entity.file.SecaFileSequence.SEQUENCE_STATUS;

/**
 *
 * @author KH
 */
public class SecaFileSequence_ /*extends EnterpriseData_*/ {
    
     public static volatile SingularAttribute<SecaFileEntity,Long> CURRENT_LINE_NUM;
     public static volatile SingularAttribute<SecaFileEntity,String> SEQUENCE_CONTENT;
     public static volatile SingularAttribute<SecaFileEntity,SEQUENCE_STATUS> STATUS;
     
}
