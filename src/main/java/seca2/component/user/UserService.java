/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.user;

import EDS.Entity.EnterpriseObject_;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.GenericJDBCException;
import org.joda.time.DateTime;
import seca2.bootstrap.GlobalValues;
import seca2.component.data.DBConnectionException;
import seca2.component.data.HibernateEMServices;
import seca2.entity.navigation.MenuItem;
import seca2.entity.user.User;
import seca2.entity.user.UserAccount;
import seca2.entity.user.UserAccount_;
import seca2.entity.user.UserPreferenceSet;
import seca2.entity.user.UserPreferenceSet_;
import seca2.entity.user.UserType;
import seca2.entity.user.UserType_;
import seca2.entity.user.User_;

/**
 *
 * @author vincent.a.lee
 */
@Stateless
public class UserService {

    private static final String HASH_KEY = "33150291203315029120";
    private static final int MAX_UNSUCCESS_ATTEMPTS = 3;

    @EJB
    private HibernateEMServices hibernateDB;

    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;

    /**
     * Should I return something like UserTypeID?
     * 
     * @param userTypeName
     * @param description
     * @throws UserTypeException
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUserType(String userTypeName, String description)
            throws UserTypeException, DBConnectionException {

        try {
            if (userTypeName == null || userTypeName.length() <= 0) {
                throw new UserTypeException("Usertype name cannot be empty!");
            }

            List<UserType> existingUserTypes = this.getUserTypeByName(userTypeName);

            if (existingUserTypes != null && !existingUserTypes.isEmpty()) {
                throw new UserTypeException("Usertype name \"" + userTypeName + "\" has been taken. Please choose a different name.");
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User createUser(long userTypeId) 
            throws UserCreationException, DBConnectionException{
        try{
            UserType type = this.getUserTypeById(userTypeId);
            if(type == null)
                throw new UserCreationException("UserType Id "+userTypeId+" not found.");
            
            User user = new User();
            user.setUSERTYPE(type);
            em.persist(user);
            return user;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerUserByUserId(long userId, String username, String password) 
            throws UserRegistrationException, DBConnectionException{
        try{
            //Check if the userId already exist by retrieving it. If no, throw exception.
            User existingUser = this.getUserById(userId);
            if(existingUser == null)
                throw new UserRegistrationException("User ID "+userId+" does not exist yet.");
            
            //Check if username is null
            if(username == null || username.isEmpty())
                throw new UserRegistrationException("Username cannot be empty.");
            
            //Check if password is null
            if(password == null || password.isEmpty())
                throw new UserRegistrationException("Password cannot be empty.");
            
            //Create the UserAccount object and link it to the User object
            UserAccount userAccount = new UserAccount();
            userAccount.setUSERNAME(username);
            userAccount.setPASSWORD(password);
            userAccount.setOWNER(existingUser);
            
            //Persist and throw any errors
            em.persist(userAccount);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerUserByUserTypeId(long userTypeId, String username, String password)
        throws UserRegistrationException, DBConnectionException{
        try{
            //Check if username is null
            if(username == null || username.isEmpty())
                throw new UserRegistrationException("Username cannot be empty.");
            
            //Check if password is null
            if(password == null || password.isEmpty())
                throw new UserRegistrationException("Password cannot be empty.");
            
            //Create a new User object
            User newUser = this.createUser(userTypeId);
            
            //Create the UserAccount object and link it to the User object
            UserAccount userAccount = new UserAccount();
            userAccount.setUSERNAME(username);
            userAccount.setPASSWORD(password);
            userAccount.setOWNER(newUser);
            
            //Persist and throw any errors
            em.persist(userAccount);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (UserCreationException ucex){
            throw new UserRegistrationException(ucex.getLocalizedMessage());
        } catch (Exception ex) {
            throw ex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserType> getAllUserTypes() throws DBConnectionException {

        try {
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
    public UserType getUserTypeById(long userTypeId) throws DBConnectionException {

        try {
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
            if (pex instanceof NoResultException) {
                return null;
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserType> getUserTypeByName(String userTypeName) throws DBConnectionException {

        try {
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public User getUserById(long userId) throws DBConnectionException{
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> sourceEntity = criteria.from(User.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(User_.OBJECTID), userId)); //WHERE USERTYPENAME = userTypeName

            User result = em.createQuery(criteria)
                    .getSingleResult();

            return result;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Returns the UserAccount object if authentication passes. If
     * authentication passes but UserAccount is locked, throw a
     * UserAccountLockedException. If authentication fails, return null and let
     * the client code handle.
     * <p>
     * Should UserServie handle session?
     *
     * @param username
     * @param password
     * @return
     * @throws UserAccountLockedException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserContainer login(String username, String password) throws UserAccountLockedException, DBConnectionException {

        String secureHash = this.getPasswordHash(username, password, HASH_KEY);

        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);

            Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserAccount
            criteria.select(sourceEntity);

            criteria.where(builder.equal(sourceEntity.get(UserAccount_.USERNAME), username));

            /**
             * We are assuming that username is unique here. If in any case
             * there are duplicated usernames, we are also assuming there is
             * only 1 valid UserAccount record for each User object (Time
             * constraint T). If there are any duplicates, an exception will be
             * thrown here.
             */
            UserAccount result = em.createQuery(criteria)
                    .getSingleResult();

            if (result == null) {
                return null;
            }
            
            //If user's account is already locked, no need to authenticate further
            if(result.isUSER_LOCKED())
                throw new UserAccountLockedException(username);
            
            if (!secureHash.equals(result.getPASSWORD())) { //authentication fails
                //increment unsuccessful counter and set lock flag
                result.setLAST_UNSUCCESS_ATTEMPT((new DateTime()).toDate());
                result.setUNSUCCESSFUL_ATTEMPTS(result.getUNSUCCESSFUL_ATTEMPTS() + 1);
                if (result.getUNSUCCESSFUL_ATTEMPTS() >= MAX_UNSUCCESS_ATTEMPTS)
                    result.setUSER_LOCKED(true);
                
                em.persist(result);
                
                return null;
            }
            
            //If authentication passes
            if(secureHash.equals(result.getPASSWORD())){
                //Only if there were any unsuccessful login attempts, reset counter
                if(result.getUNSUCCESSFUL_ATTEMPTS() > 0){
                    result.setUNSUCCESSFUL_ATTEMPTS(0);
                    em.persist(result);
                }
                /**
                 * Should construct and return a UserContainer instead of the User 
                 * or UserAccount object. User object is useless and UserAccount
                 * object would contain passwords.
                 */
                User user = (User) result.getOWNER();
                UserPreferenceSet preferences = this.getUserPreferences(user.getOBJECTID());
                
                UserContainer uc = new UserContainer();
                uc.setPreferences(preferences);
                uc.setUser(user);
                uc.setUserType(user.getUSERTYPE());
                
                return uc;
                
                
            }
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
        
        throw new RuntimeException("This code has not handled certain cases yet!");
    }
    
    public UserPreferenceSet getUserPreferences(long userid){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<UserPreferenceSet> criteria = builder.createQuery(UserPreferenceSet.class);

        Root<UserPreferenceSet> sourceEntity = criteria.from(UserPreferenceSet.class); //FROM UserAccount
        criteria.select(sourceEntity);

        criteria.where(builder.equal(sourceEntity.get(UserPreferenceSet_.OWNER), userid));
        
        UserPreferenceSet result = em.createQuery(criteria)
                .getSingleResult();
        
        return result;
    }

    /**
     * Helper method
     *
     * @param username
     * @param password
     * @param exraHash
     * @return
     */
    private String getPasswordHash(String username, String password, String exraHash) {
        String secureHash = password.concat(username).concat(exraHash);
        MessageDigest md;
        byte[] hash;
        try {
            md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(secureHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException("Error encountered in login method.");
        }
        String hashedPassword = String.format("%032x", new BigInteger(hash));

        return hashedPassword;
    }

}
