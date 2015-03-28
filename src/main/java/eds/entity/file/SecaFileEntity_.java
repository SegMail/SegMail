/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.file;


import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import eds.entity.file.SecaFileEntity.FILE_STATUS;

/**
 *
 * @author KH
 */
@StaticMetamodel(SecaFileEntity.class)
public class SecaFileEntity_ /*extends EnterpriseUnit_*/{
    
    public static volatile SingularAttribute<SecaFileEntity,String> FILENAME;
    public static volatile SingularAttribute<SecaFileEntity,Long> BYTE_SIZE;
    public static volatile SingularAttribute<SecaFileEntity,Long> SEQUENCE_SIZE;
    public static volatile SingularAttribute<SecaFileEntity,FILE_STATUS> STATUS;
    public static volatile ListAttribute<SecaFileEntity,SecaFileSequence> sequences;
}
