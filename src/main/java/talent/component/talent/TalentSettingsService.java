/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.component.talent;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.config.GenericConfigService;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import talent.entity.people.performance.PerformanceLevel;
import talent.entity.people.performance.PerformanceLevel_;
import talent.entity.people.potential.PotentialLevel;
import talent.entity.people.potential.PotentialLevel_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class TalentSettingsService {
    
    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;
    
    @EJB private GenericObjectService objectService;
    @EJB private GenericConfigService configService;
    
    /**
     * Retrieves all potential levels tagged with the value provided. 
     * 
     * @param value
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PotentialLevel> getPotentialLevelByValue(int value){
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<PotentialLevel> query = builder.createQuery(PotentialLevel.class);
            Root<PotentialLevel> potential = query.from(PotentialLevel.class);
            
            query.select(potential);
            query.where(builder.equal(potential.get(PotentialLevel_.VALUE), value));
            
            List<PotentialLevel> results = em.createQuery(query)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PotentialLevel> getPotentialLevelByName(String name){
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<PotentialLevel> query = builder.createQuery(PotentialLevel.class);
            Root<PotentialLevel> potential = query.from(PotentialLevel.class);
            
            query.select(potential);
            query.where(builder.equal(potential.get(PotentialLevel_.LEVEL_NAME), name));
            
            List<PotentialLevel> results = em.createQuery(query)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PotentialLevel createNewPotentialLevel(String name, int value) throws EntityExistsException{
        try {
            // Check if the potential level already exists with the value
            List<PotentialLevel> existingPotential = getPotentialLevelByValue(value);
            if(!existingPotential.isEmpty())
                throw new EntityExistsException("Potential level already exists!");
            
            existingPotential.addAll(getPotentialLevelByName(name));
            if(!existingPotential.isEmpty())
                throw new EntityExistsException("Potential name already exists!");
            
            PotentialLevel newLevel = new PotentialLevel();
            //newLevel.setLEVEL_LABEL(label);
            newLevel.setLEVEL_NAME(name);
            newLevel.setLEVEL_VALUE(value);
            
            em.persist(newLevel);
            
            return newLevel;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PerformanceLevel> getPerformanceLevelByValue(int rating){
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<PerformanceLevel> query = builder.createQuery(PerformanceLevel.class);
            Root<PerformanceLevel> potential = query.from(PerformanceLevel.class);
            
            query.select(potential);
            query.where(builder.equal(potential.get(PerformanceLevel_.RATING), rating));
            
            List<PerformanceLevel> results = em.createQuery(query)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PerformanceLevel> getPerformanceLevelByName(String name){
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<PerformanceLevel> query = builder.createQuery(PerformanceLevel.class);
            Root<PerformanceLevel> potential = query.from(PerformanceLevel.class);
            
            query.select(potential);
            query.where(builder.equal(potential.get(PerformanceLevel_.LEVEL_NAME), name));
            
            List<PerformanceLevel> results = em.createQuery(query)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PerformanceLevel createPerformanceLevelLevel(String name, int rating) throws EntityExistsException{
        try {
            // Check if the performance level already exists with the value
            List<PerformanceLevel> existingPerformance = getPerformanceLevelByValue(rating);
            if(!existingPerformance.isEmpty())
                throw new EntityExistsException("Performance level already exists!");
            
            existingPerformance.addAll(getPerformanceLevelByName(name));
            if(!existingPerformance.isEmpty())
                throw new EntityExistsException("Performance name already exists!");
            
            PerformanceLevel newLevel = new PerformanceLevel();
            //newLevel.setLEVEL_LABEL(label);
            newLevel.setLEVEL_NAME(name);
            newLevel.setRATING(rating);
            
            em.persist(newLevel);
            
            return newLevel;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    public boolean isTalentPoolEmpty(long clientid){
        try {
            return true;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
}
