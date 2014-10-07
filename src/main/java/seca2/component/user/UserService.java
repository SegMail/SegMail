/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.user;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import seca2.bootstrap.GlobalValues;
import seca2.component.data.DBConnectionException;
import seca2.component.data.HibernateEMServices;
import seca2.entity.navigation.MenuItem;
import seca2.entity.user.UserType;
import seca2.entity.user.UserType_;

/**
 *
 * @author vincent.a.lee
 */
@Stateless
public class UserService  {
    private static final String HASH_KEY = "33150291203315029120";
    private static final int MAX_UNSUCCESS_ATTEMPTS = 3;
    
    @EJB private HibernateEMServices hibernateDB;
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUserType(String userTypeName, String description)
        throws UserTypeException, DBConnectionException{
                
        try{
            if(userTypeName == null || userTypeName.length() <= 0)
                throw new UserTypeException("Usertype name cannot be empty!");
            
            List<UserType> existingUserTypes = this.getUserTypeByName(userTypeName);
            
            if(existingUserTypes != null && !existingUserTypes.isEmpty()){
                throw new UserTypeException("Usertype name \""+userTypeName+"\" has been taken. Please choose a different name.");
            }
            
            //Instantiate the UserType object
            UserType userType = new UserType();
            userType.setUSERTYPENAME(userTypeName);
            
            //em.getTransaction().begin();
            em.persist(userType);
            //em.getTransaction().commit();
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserType> getAllUserTypes() throws DBConnectionException{
        
        try{
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
            Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            
            List<UserType> results = em.createQuery(criteria)
                    .setFirstResult(0)
                    .setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserType getUserTypeById(long userTypeId) throws DBConnectionException{
        
        try{
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
            Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(UserType_.OBJECTID), userTypeId)); //WHERE USERTYPENAME = userTypeName
            
            UserType result = em.createQuery(criteria)
                    .getSingleResult();
            
            return result;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            if (pex instanceof NoResultException){
                return null;
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserType> getUserTypeByName(String userTypeName) throws DBConnectionException{
        
        try{
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
            Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(UserType_.USERTYPENAME), userTypeName)); //WHERE USERTYPENAME = userTypeName
            
            List<UserType> results = em.createQuery(criteria)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
