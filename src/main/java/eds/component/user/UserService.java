/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.user;

import eds.component.GenericObjectService;
import eds.entity.data.EnterpriseObject;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import org.joda.time.DateTime;
import eds.component.DBService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.HibernateHelper;
import eds.component.data.IncompleteDataException;
import eds.entity.user.APIAccount;
import eds.entity.user.APIAccount_;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import eds.entity.user.UserAccount_;
import eds.entity.user.UserPreferenceSet;
import eds.entity.user.UserPreferenceSet_;
import eds.entity.user.UserType;
import eds.entity.user.UserType_;
import eds.entity.user.User_;
import javax.ejb.EJB;
import javax.ejb.EJBException;

/**
 *
 * @author vincent.a.lee
 */
@Stateless
public class UserService extends DBService {

    private static final String HASH_KEY = "33150291203315029120";
    private static final int MAX_UNSUCCESS_ATTEMPTS = 3;
    
    @Resource()
    private String US_USER;
    
    
    @EJB private GenericObjectService objectService;

    /**
     * Creates and returns a new UserType entity. If userTypeName is empty or null,
     * throw an IncompleteDataException. If username is taken, throw an EntityExistsException.
     * 
     * @param userTypeName
     * @param description
     * @return the newly created UserType object
     * @throws eds.component.data.IncompleteDataException
     * @throws eds.component.data.EntityExistsException
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserType createUserType(String userTypeName, String description, boolean portalAccess, boolean wsAccess) 
            throws IncompleteDataException, EntityExistsException
    {
        try {
            if (userTypeName == null || userTypeName.length() <= 0) {
                throw new IncompleteDataException("Usertype name cannot be empty!");
            }

            List<UserType> existingUserTypes = this.getUserTypeByName(userTypeName);

            if (existingUserTypes != null && !existingUserTypes.isEmpty()) {
                throw new EntityExistsException("Usertype name \"" + userTypeName + "\" has been taken. Please choose a different name.");
            }

            //Instantiate the UserType object
            UserType userType = new UserType();
            userType.setUSERTYPENAME(userTypeName);
            userType.setPORTAL_ACCESS(portalAccess);
            userType.setWS_ACCESS(wsAccess);

            objectService.getEm().persist(userType);
            
            return userType;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User createUser(long userTypeId) 
            throws DBConnectionException, EntityNotFoundException{
        try{
            UserType type = this.getUserTypeById(userTypeId);
            if(type == null)
                throw new EntityNotFoundException(UserType.class,userTypeId);
            
            User user = new User();
            user.setUSERTYPE(type);
            objectService.getEm().persist(user);
            //debug
            System.out.println("The user id is "+user.getOBJECTID());
            return user;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserAccount registerUserByUserTypeId(long userTypeId, String username, String password)
        throws EntityNotFoundException, IncompleteDataException, EntityExistsException{
        try{
            //Check if username is null
            if(username == null || username.isEmpty())
                throw new IncompleteDataException("Username cannot be empty.");
            
            //Check if password is null
            if(password == null || password.isEmpty())
                throw new IncompleteDataException("Password cannot be empty.");
            
            //Check if username has been used
            User existingUser = this.getUserByUsername(username);
            if(existingUser != null)
                throw new EntityExistsException(existingUser);
            
            //Create a new User object first
            User newUser = this.createUser(userTypeId);
            
            //Impt! Set the username to the user object's name
            newUser.setOBJECT_NAME(username);
            
            //Hash passwords
            String hashedPassword = this.getPasswordHash(username, password, HASH_KEY);
            
            //Create the UserAccount object and link it to the User object
            UserAccount userAccount = new UserAccount();
            userAccount.setUSERNAME(username);
            userAccount.setPASSWORD(hashedPassword);
            userAccount.setOWNER(newUser);
            
            //Persist and throw any errors
            objectService.getEm().persist(userAccount);
            
            return userAccount;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserType> getAllUserTypes() throws DBConnectionException {

        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
            Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType

            criteria.select(sourceEntity); // SELECT *

            List<UserType> results = objectService.getEm().createQuery(criteria)
                    //.setFirstResult(0)
                    //.setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB) //Don't do this first, not necessary!
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
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
            Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType

            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(UserType_.OBJECTID), userTypeId)); //WHERE USERTYPENAME = userTypeName

            UserType result = objectService.getEm().createQuery(criteria)
                    .getSingleResult(); //appropriate! because it is an EO

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
    public List<UserType> getUserTypeByName(String userTypeName) {

        try {
            /*CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
            Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType

            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(UserType_.USERTYPENAME), userTypeName)); //WHERE USERTYPENAME = userTypeName
            
            List<UserType> results = em.createQuery(criteria)
                    .getResultList();
            */
            List<UserType> results = this.objectService.getEnterpriseObjectsByName(userTypeName, UserType.class);

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
    
    /**
     * Returns the UserType entity with the exact matching userTypeName.
     * 
     * @param userTypeName
     * @return
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserType getSingleUserTypeByName(String userTypeName) {

        try {
            
            List<UserType> results = this.getUserTypeByName(userTypeName);
            
            if(results == null || results.isEmpty())
                return null;

            return results.get(0);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public User getUserById(long userId) throws DBConnectionException{
        try {
            /*CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> sourceEntity = criteria.from(User.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(User_.OBJECTID), userId)); //WHERE USERTYPENAME = userTypeName

            User result = em.createQuery(criteria)
                    .getSingleResult();//appropriate! because it is an EO*/
            
            User result = this.objectService.getEnterpriseObjectById(userId, User.class);

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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public User getUserByUsername(String username) throws DBConnectionException{
        try {
            UserAccount userAccount = this.getUserAccountByUsername(username);
            
            if(userAccount == null)
                return null;

            return userAccount.getOWNER();

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
     * Should UserServie handle session? [20150315] Nope at this moment. Session
     * is handled by the Web presentation layer, unless we're talking about Web 
     * Services.
     * <p>
     * Should we return the UserAccount object? Ok, since I have no idea what I
     * want to return to the client upon login, I will just dunp anything in a 
     * Map<String,Object> object.
     * <p>
     * [20150321] Returning the User object is safe as you can't get any EnterpriseData
     * if you do not know the table name. Anyway, no reason restricting access at this level.
     *
     * @param username
     * @param password
     * @param userValues
     * @throws UserAccountLockedException
     * @throws UserLoginException
     * @throws eds.component.data.DBConnectionException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void login(String username, String password, Map<String,Object> userValues) 
            throws UserAccountLockedException,
                   UserLoginException, 
                   DBConnectionException {
        try {
            //Check if username exists
            UserAccount userAccount = this.getUserAccountByUsername(username);
            if(userAccount == null) //Do not tell user that username does not exist for security reasons
                throw new UserLoginException("Wrong credentials.");
            
            //If user's account is already locked, no need to authenticate further
            if(userAccount.isUSER_LOCKED())
                throw new UserAccountLockedException(username);
            
            String secureHash = this.getPasswordHash(username, password, HASH_KEY);
            if (!secureHash.equals(userAccount.getPASSWORD())) { //authentication fails
                //increment unsuccessful counter and set lock flag
                userAccount.setLAST_UNSUCCESS_ATTEMPT((new DateTime()).toDate());
                userAccount.setUNSUCCESSFUL_ATTEMPTS(userAccount.getUNSUCCESSFUL_ATTEMPTS() + 1);
                if (userAccount.getUNSUCCESSFUL_ATTEMPTS() >= MAX_UNSUCCESS_ATTEMPTS)
                    userAccount.setUSER_LOCKED(true);
                
                objectService.getEm().persist(userAccount);
                
                throw new UserLoginException("Wrong credentials.");
            }
            
            //If authentication passes
            if(secureHash.equals(userAccount.getPASSWORD())){
                //Only if there were any unsuccessful login attempts, reset counter
                if(userAccount.getUNSUCCESSFUL_ATTEMPTS() > 0){
                    userAccount.setUNSUCCESSFUL_ATTEMPTS(0);
                    objectService.getEm().persist(userAccount);
                }
                /**
                 * Should construct and return a UserContainer instead of the User 
                 * or UserAccount object. User object is useless and UserAccount
                 * object would contain passwords.
                 * 
                 * 
                 */
                EnterpriseObject owner = userAccount.getOWNER();
                
                User user = (User) HibernateHelper.initializeAndUnproxy(owner);
                
                List<UserPreferenceSet> preferences = this.getUserPreferences(user.getOBJECTID());
                
                userValues.put(this.US_USER, user);
                /*
                userValues.put(USER, user);
                userValues.put(USER_TYPE, user.getUSERTYPE());
                userValues.put(USER_ATTRIBUTES, preferences);
                */
                System.out.println("");//debug
                
            } else {
                throw new UserLoginException("UserService: Something not handled yet!");
            }
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
        catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User login(String username, String password) 
            throws UserAccountLockedException,
                   UserLoginException 
                    {
        try {
            //Check if username is empty
            if(username == null || username.length() <= 0)
                throw new UserLoginException("Please enter username.");
            
            //Check if password is empty
            if(password == null || password.length() <= 0)
                throw new UserLoginException("Please enter password.");
            
            //Check if username exists
            UserAccount userAccount = this.getUserAccountByUsername(username);
            if(userAccount == null) //Do not tell user that username does not exist for security reasons
                throw new UserLoginException("Wrong credentials.");
            
            //If user's account is already locked, no need to authenticate further
            if(userAccount.isUSER_LOCKED())
                throw new UserAccountLockedException(username);
            
            String secureHash = this.getPasswordHash(username, password, HASH_KEY);
            if (!secureHash.equals(userAccount.getPASSWORD())) { //authentication fails
                //increment unsuccessful counter and set lock flag
                userAccount.setLAST_UNSUCCESS_ATTEMPT((new DateTime()).toDate());
                userAccount.setUNSUCCESSFUL_ATTEMPTS(userAccount.getUNSUCCESSFUL_ATTEMPTS() + 1);
                if (userAccount.getUNSUCCESSFUL_ATTEMPTS() >= MAX_UNSUCCESS_ATTEMPTS)
                    userAccount.setUSER_LOCKED(true);
                
                objectService.getEm().persist(userAccount);
                
                throw new UserLoginException("Wrong credentials.");
            }
            
            //If authentication passes
            if(secureHash.equals(userAccount.getPASSWORD())){
                //Only if there were any unsuccessful login attempts, reset counter
                if(userAccount.getUNSUCCESSFUL_ATTEMPTS() > 0){
                    userAccount.setUNSUCCESSFUL_ATTEMPTS(0);
                    objectService.getEm().persist(userAccount);
                }
                /**
                 * Should construct and return a UserContainer instead of the User 
                 * or UserAccount object. User object is useless and UserAccount
                 * object would contain passwords.
                 * 
                 * 
                 */
                EnterpriseObject owner = userAccount.getOWNER();
                
                User user = (User) HibernateHelper.initializeAndUnproxy(owner);
                
                return user;
                
            } else {
                throw new RuntimeException("UserService: Something not handled yet!");
            }
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserPreferenceSet> getUserPreferences(long userid){
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<UserPreferenceSet> criteria = builder.createQuery(UserPreferenceSet.class);

        Root<UserPreferenceSet> sourceEntity = criteria.from(UserPreferenceSet.class); //FROM UserAccount
        criteria.select(sourceEntity);

        criteria.where(builder.equal(sourceEntity.get(UserPreferenceSet_.OWNER), userid));
        
        /* Faulty assumption to use getSingleResult()
        * getSingleResult() throws exception as long as there is no 1 single result 
        * returned!
        *
        *UserPreferenceSet result = em.createQuery(criteria)
        *       .getSingleResult();
        */
        
        List<UserPreferenceSet> results = objectService.getEm().createQuery(criteria)
                .getResultList();
        
        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserAccount getUserAccountByUsername(String username) 
            throws DBConnectionException{
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);
            Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(UserAccount_.USERNAME), username)); //WHERE USERTYPENAME = userTypeName
            
            //Temporary measure before we find a better way to define the underlying
            //data of UserAccount object and subsequently how to retrieve the correct
            //result.
            //
            List<UserAccount> results = objectService.getEm().createQuery(criteria)
                    .getResultList();
            
            if(results == null || results.size() <= 0)
                return null;

            return results.get(0);

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
    public UserAccount getUserAccountById(long userid) 
            throws DBConnectionException{
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);
            Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(UserAccount_.OWNER), userid)); //WHERE USERTYPENAME = userTypeName
            
            //Temporary measure before we find a better way to define the underlying
            //data of UserAccount object and subsequently how to retrieve the correct
            //result.
            //
            List<UserAccount> results = objectService.getEm().createQuery(criteria)
                    .getResultList();
            
            if(results == null || results.size() <= 0)
                return null;

            return results.get(0);

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
    public boolean checkUsernameExist(String username) throws DBConnectionException{
        try{
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserAccount
            
            criteria.select(builder.count(criteria.from(UserAccount.class))); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(UserAccount_.USERNAME), username)); //WHERE USERNAME = username
            
            Long result = objectService.getEm().createQuery(criteria)
                    .getSingleResult();
            
            return result > 0;
            
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
    public long getUserCount() throws DBConnectionException{
        try{
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserAccount
            
            criteria.select(builder.count(criteria.from(UserAccount.class))); // SELECT *
            
            Long result = objectService.getEm().createQuery(criteria)
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getUserProfilePicLocation(long userid) throws DBConnectionException{
        
        try{
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<String> criteria = builder.createQuery(String.class);
            Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserAccount
            
            criteria.select(sourceEntity.get(UserAccount_.PROFILE_PIC_URL)); // SELECT PROFILE_PIC_URL
            criteria.where(builder.equal(sourceEntity.get(UserAccount_.OWNER), userid)); //WHERE OWNER.OBJECT_ID = userid
            
            List<String> results = objectService.getEm().createQuery(criteria)
                    .getResultList();
            
            if(results == null || results.size() <= 0)
                return null;

            return results.get(0);
            
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
    public void setProfilePicLocationForUserid(long userid, String profilePicLocation)
            throws EntityNotFoundException{
        
        try{
            //Get the useraccount object
            UserAccount userAccount = this.getUserAccountById(userid);
            
            if(userAccount == null)
                throw new EntityNotFoundException(User.class,userid);
            
            userAccount.setPROFILE_PIC_URL(profilePicLocation);
            
            objectService.getEm().persist(userAccount);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setProfilePicLocationForUsername(String username, String profilePicLocation) 
            throws EntityNotFoundException {
        
        try{
            //Get the useraccount object
            UserAccount userAccount = this.getUserAccountByUsername(username);
            
            if(userAccount == null)
                throw new EntityNotFoundException(username);
            
            userAccount.setPROFILE_PIC_URL(profilePicLocation);
            
            objectService.getEm().persist(userAccount);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public APIAccount getAPIAccountById(long userid) 
            throws DBConnectionException{
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<APIAccount> criteria = builder.createQuery(APIAccount.class);
            Root<APIAccount> sourceEntity = criteria.from(APIAccount.class); //FROM UserType
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(APIAccount_.OWNER), userid)); //WHERE USERTYPENAME = userTypeName
            
            //Temporary measure before we find a better way to define the underlying
            //data of UserAccount object and subsequently how to retrieve the correct
            //result.
            //
            List<APIAccount> results = objectService.getEm().createQuery(criteria)
                    .getResultList();
            
            if(results == null || results.size() <= 0)
                return null;

            return results.get(0);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String generateAPIKey(long userid) 
            throws EntityNotFoundException{
        try{
            //Get user object first
            User user = this.getUserById(userid);
            if(user == null)
                throw new EntityNotFoundException(User.class,userid);
            
            
            return null;
        }  catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserAccount> getWebServiceUserAccounts() {
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);
            Root<UserAccount> fromUserAccount = criteria.from(UserAccount.class);
            Root<User> fromUser = criteria.from(User.class);
            Root<UserType> fromUserType = criteria.from(UserType.class);
            
            criteria.select(fromUserAccount).distinct(true); // SELECT *
            criteria.where(builder.and(
                    builder.isTrue(fromUserType.get(UserType_.WS_ACCESS)),
                    builder.equal(fromUserType.get(UserType_.OBJECTID), fromUser.get(User_.USERTYPE)),
                    builder.equal(fromUserAccount.get(UserAccount_.OWNER), fromUser.get(User_.OBJECTID))
            ));
            
            List<UserAccount> results = objectService.getEm().createQuery(criteria)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
}
