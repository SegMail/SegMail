/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.file;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.entity.file.FileTransaction;
import eds.entity.file.FileTransaction_;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class FileService {
    
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    @EJB SubscriptionService subService;
    
    public List<FileTransaction> getFileTransactions(String filename, String fileHash){
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<FileTransaction> query = builder.createQuery(FileTransaction.class);
        Root<FileTransaction> fromFile = query.from(FileTransaction.class);
        
        query.select(fromFile);
        query.where(
                builder.and(
                        builder.equal(fromFile.get(FileTransaction_.NAME), filename),
                        builder.equal(fromFile.get(FileTransaction_.CHECKSUM), fileHash)
                )
        );
        
        List<FileTransaction> results = objService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
    /**
     * 
     * @param filename
     * @param fileHash
     * @param location
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileTransaction createOrGetFileTransaction(String filename, String fileHash, String location) {
        List<FileTransaction> existingFiles = getFileTransactions(filename, fileHash);
        FileTransaction newFile = new FileTransaction();
        
        if(existingFiles == null || existingFiles.isEmpty()) {
            newFile.setNAME(filename);
            newFile.setCHECKSUM(fileHash);
            newFile.setLOCATION(location);
            newFile.setLAST_PROCESSING_POSITION(-1);
            
            updService.getEm().persist(newFile);
            
            existingFiles = new ArrayList<>();
            existingFiles.add(newFile);
        }
            
        return existingFiles.get(0);
    }
}
