/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.user;

import eds.entity.user.PWD_PROCESSING_STATUS;
import eds.entity.user.PasswordResetRequest;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import eds.component.DBService;
import eds.component.UpdateObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.HibernateHelper;
import eds.component.data.IncompleteDataException;
import eds.component.encryption.EncryptionType;
import eds.component.encryption.EncryptionUtility;
import eds.component.mail.MailServiceOutbound;
import eds.component.transaction.TransactionNotFoundException;
import eds.component.transaction.TransactionService;
import eds.entity.user.PasswordResetRequest_;
import eds.entity.user.Trigger_Password_User;
import eds.entity.user.Trigger_Password_User_;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import eds.entity.user.UserAccount_;
import eds.entity.user.UserType;
import eds.entity.user.UserType_;
import eds.entity.user.User_;
import javax.ejb.EJB;
import org.apache.commons.validator.routines.EmailValidator;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author vincent.a.lee
 */
@Stateless
public class UserService extends DBService {

    private static final String HASH_KEY = "33150291203315029120";
    private static final int MAX_UNSUCCESS_ATTEMPTS = 3;
    
    public static final String PASSWORD_RESET_EMAIL_TYPE = "PASSWORD_RESET";
    public static final String ADMIN_EMAIL = "support@segmail.io";

    @EJB
    GenericObjectService objectService;
    @EJB
    UpdateObjectService updService;
    @EJB
    MailServiceOutbound mailService;
    @EJB
    LandingService landingService;
    @EJB
    TransactionService txService;

    /**
     * Creates and returns a new UserType entity. If userTypeName is empty or
     * null, throw an IncompleteDataException. If username is taken, throw an
     * EntityExistsException.
     *
     * @param userTypeName
     * @param description
     * @return the newly created UserType object
     * @throws eds.component.data.IncompleteDataException
     * @throws eds.component.data.EntityExistsException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserType createUserType(String userTypeName, String description, boolean portalAccess, boolean wsAccess)
            throws IncompleteDataException, EntityExistsException {
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
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User createUser(long userTypeId)
            throws EntityNotFoundException {
        UserType type = this.getUserTypeById(userTypeId);
        if (type == null) {
            throw new EntityNotFoundException(UserType.class, userTypeId);
        }

        User user = new User();
        user.setUSERTYPE(type);
        objectService.getEm().persist(user);

        return user;
    }

    /**
     * 
     * @param userTypeId
     * @param username
     * @param password
     * @param contact email contact
     * @return
     * @throws EntityNotFoundException 
     * @throws IncompleteDataException
     * @throws EntityExistsException if email was already registered
     * @throws DataValidationException if contact email is invalid or username is taken
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserAccount registerUserByUserTypeId(long userTypeId, String username, String password, String contact)
            throws EntityNotFoundException, IncompleteDataException, EntityExistsException, DataValidationException {
        //Check if username is null
        if (username == null || username.isEmpty()) {
            throw new IncompleteDataException("Username cannot be empty.");
        }

        //Check if password is null
        if (password == null || password.isEmpty()) {
            throw new IncompleteDataException("Password cannot be empty.");
        }
        
        if (contact == null || contact.isEmpty()) {
            throw new IncompleteDataException("Contact email cannot be empty.");
        }
        
        if (!EmailValidator.getInstance().isValid(contact)) {
            throw new DataValidationException("Contact email is invalid.");
        }
        
        //Important! Check if the user already has an account by using email,
        //not username, because email is an external identifier while username is
        //internal. 
        UserAccount existingUser = this.getUserAccountByContactEmail(contact);
        if (existingUser != null) {
            throw new EntityExistsException("An account with this email address has already been registered.");
        }

        //Check if username has been used
        existingUser = this.getUserAccountByUsername(username);
        if (existingUser != null) {
            throw new DataValidationException("Username is already taken.");
        }

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
        userAccount.setAPI_KEY(generateUserAPIKey(newUser));
        userAccount.setCONTACT_EMAIL(contact);

        //Persist and throw any errors
        objectService.getEm().persist(userAccount);
        
        return userAccount;
    }
    

    
    public List<UserType> getAllUserTypes() {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
        Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType

        criteria.select(sourceEntity); // SELECT *

        List<UserType> results = objectService.getEm().createQuery(criteria)
                //.setFirstResult(0)
                //.setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB) //Don't do this first, not necessary!
                .getResultList();

        return results;
    }

    
    public UserType getUserTypeById(long userTypeId) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<UserType> criteria = builder.createQuery(UserType.class);
        Root<UserType> sourceEntity = criteria.from(UserType.class); //FROM UserType

        criteria.select(sourceEntity); // SELECT *
        criteria.where(builder.equal(sourceEntity.get(UserType_.OBJECTID), userTypeId)); //WHERE USERTYPENAME = userTypeName

        UserType result = objectService.getEm().createQuery(criteria)
                .getSingleResult(); //appropriate! because it is an EO

        return result;
    }

    
    public List<UserType> getUserTypeByName(String userTypeName) {
        List<UserType> results = this.objectService.getEnterpriseObjectsByName(userTypeName, UserType.class);

        return results;
    }

    /**
     * Returns the UserType entity with the exact matching userTypeName.
     *
     * @param userTypeName
     * @return
     */
    
    public UserType getSingleUserTypeByName(String userTypeName) {
        List<UserType> results = this.getUserTypeByName(userTypeName);

        if (results == null || results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    
    public User getUserById(long userId) {
        User result = this.objectService.getEnterpriseObjectById(userId, User.class);

        return result;
    }
    
    public User getUserByUsername(String username) {
        UserAccount userAccount = this.getUserAccountByUsername(username);

        if (userAccount == null) {
            return null;
        }

        return userAccount.getOWNER();
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
     * [20150321] Returning the User object is safe as you can't get any
     * EnterpriseData if you do not know the table name. Anyway, no reason
     * restricting access at this level.
     *
     * @param username
     * @param password
     * @param userValues
     * @throws UserAccountLockedException
     * @throws UserLoginException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void login(String username, String password, Map<String, Object> userValues)
            throws UserAccountLockedException, UserLoginException {
        //Check if username exists
        UserAccount userAccount = this.getUserAccountByUsername(username);
        if (userAccount == null) //Do not tell user that username does not exist for security reasons
        {
            throw new UserLoginException("Wrong credentials.");
        }

        //If user's account is already locked, no need to authenticate further
        if (userAccount.isUSER_LOCKED()) {
            throw new UserAccountLockedException(username);
        }

        String secureHash = this.getPasswordHash(username, password, HASH_KEY);
        if (!secureHash.equals(userAccount.getPASSWORD())) { //authentication fails
            //increment unsuccessful counter and set lock flag
            userAccount.setLAST_UNSUCCESS_ATTEMPT((new DateTime()).toDate());
            userAccount.setUNSUCCESSFUL_ATTEMPTS(userAccount.getUNSUCCESSFUL_ATTEMPTS() + 1);
            if (userAccount.getUNSUCCESSFUL_ATTEMPTS() >= MAX_UNSUCCESS_ATTEMPTS) {
                userAccount.setUSER_LOCKED(true);
            }

            objectService.getEm().persist(userAccount);

            throw new UserLoginException("Wrong credentials.");
        }

        //If authentication passes
        if (secureHash.equals(userAccount.getPASSWORD())) {
            //Only if there were any unsuccessful login attempts, reset counter
            if (userAccount.getUNSUCCESSFUL_ATTEMPTS() > 0) {
                userAccount.setUNSUCCESSFUL_ATTEMPTS(0);
                objectService.getEm().persist(userAccount);
            }
            return;
        }
        throw new UserLoginException("UserService: Something not handled yet!");
        
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User login(String username, String password)
            throws UserAccountLockedException,
            UserLoginException {
        //Check if username is empty
        if (username == null || username.length() <= 0) {
            throw new UserLoginException("Please enter username.");
        }

        //Check if password is empty
        if (password == null || password.length() <= 0) {
            throw new UserLoginException("Please enter password.");
        }

        //Check if username exists
        UserAccount userAccount = this.getUserAccountByUsername(username);
        if (userAccount == null) //Do not tell user that username does not exist for security reasons
        {
            throw new UserLoginException("Wrong credentials.");
        }

        //If user's account is already locked, no need to authenticate further
        if (userAccount.isUSER_LOCKED()) {
            throw new UserAccountLockedException(username);
        }

        String secureHash = this.getPasswordHash(username, password, HASH_KEY);
        if (!secureHash.equals(userAccount.getPASSWORD())) { //authentication fails
            //increment unsuccessful counter and set lock flag
            userAccount.setLAST_UNSUCCESS_ATTEMPT((new DateTime()).toDate());
            userAccount.setUNSUCCESSFUL_ATTEMPTS(userAccount.getUNSUCCESSFUL_ATTEMPTS() + 1);
            if (userAccount.getUNSUCCESSFUL_ATTEMPTS() >= MAX_UNSUCCESS_ATTEMPTS) {
                userAccount.setUSER_LOCKED(true);
            }

            objectService.getEm().persist(userAccount);

            throw new UserLoginException("Wrong credentials.");
        }

        //If authentication passes
        if (secureHash.equals(userAccount.getPASSWORD())) {
            //Only if there were any unsuccessful login attempts, reset counter
            if (userAccount.getUNSUCCESSFUL_ATTEMPTS() > 0) {
                userAccount.setUNSUCCESSFUL_ATTEMPTS(0);
                objectService.getEm().persist(userAccount);
            }
            /**
             * Should construct and return a UserContainer instead of the
             * User or UserAccount object. User object is useless and
             * UserAccount object would contain passwords.
             *
             *
             */
            EnterpriseObject owner = userAccount.getOWNER();

            User user = (User) HibernateHelper.initializeAndUnproxy(owner);

            return user;

        } else {
            throw new RuntimeException("UserService: Something not handled yet!");
        }
    }

    //@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserAccount getUserAccountByUsername(String username) {
        
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

        if (results == null || results.size() <= 0) {
            return null;
        }

        return results.get(0); 
    }

    
    public UserAccount getUserAccountById(long userid) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);
        Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserType

        criteria.select(sourceEntity); // SELECT *
        criteria.where(builder.equal(sourceEntity.get(UserAccount_.OWNER), userid)); //WHERE USERTYPENAME = userTypeName

        //Temporary measure before we find a better way to define the underlying
        //data of UserAccount object and subsequently how to retrieve the correct
        //result.
        List<UserAccount> results = objectService.getEm().createQuery(criteria)
                .getResultList();

        if (results == null || results.size() <= 0) {
            return null;
        }

        return results.get(0);
    }

    
    public boolean checkUsernameExist(String username) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserAccount

        criteria.select(builder.count(criteria.from(UserAccount.class))); // SELECT *
        criteria.where(builder.equal(sourceEntity.get(UserAccount_.USERNAME), username)); //WHERE USERNAME = username

        Long result = objectService.getEm().createQuery(criteria)
                .getSingleResult();

        return result > 0;
    }
    
    public String getUserProfilePicLocation(long userid) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> criteria = builder.createQuery(String.class);
        Root<UserAccount> sourceEntity = criteria.from(UserAccount.class); //FROM UserAccount

        criteria.select(sourceEntity.get(UserAccount_.PROFILE_PIC_URL)); // SELECT PROFILE_PIC_URL
        criteria.where(builder.equal(sourceEntity.get(UserAccount_.OWNER), userid)); //WHERE OWNER.OBJECT_ID = userid

        List<String> results = objectService.getEm().createQuery(criteria)
                .getResultList();

        if (results == null || results.size() <= 0) {
            return null;
        }

        return results.get(0);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setProfilePicLocationForUserid(long userid, String profilePicLocation)
            throws EntityNotFoundException {
        //Get the useraccount object
        UserAccount userAccount = this.getUserAccountById(userid);

        if (userAccount == null) {
            throw new EntityNotFoundException(User.class, userid);
        }

        userAccount.setPROFILE_PIC_URL(profilePicLocation);

        objectService.getEm().persist(userAccount);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setProfilePicLocationForUsername(String username, String profilePicLocation)
            throws EntityNotFoundException {
        //Get the useraccount object
        UserAccount userAccount = this.getUserAccountByUsername(username);

        if (userAccount == null) {
            throw new EntityNotFoundException(username);
        }

        userAccount.setPROFILE_PIC_URL(profilePicLocation);

        objectService.getEm().persist(userAccount);
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
            md = MessageDigest.getInstance(EncryptionType.SHA256.toString());
            hash = md.digest(secureHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException("Error encountered in login method.");
        }
        String hashedPassword = String.format("%032x", new BigInteger(hash));

        return hashedPassword;
    }

    
    public List<UserAccount> getWebServiceUserAccounts() {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);
        Root<UserAccount> fromUserAccount = criteria.from(UserAccount.class);
        Root<User> fromUser = criteria.from(User.class);
        Root<UserType> fromUserType = criteria.from(UserType.class);

        criteria.select(fromUserAccount).distinct(true); 
        criteria.where(builder.and(
                builder.isTrue(fromUserType.get(UserType_.WS_ACCESS)),
                builder.equal(fromUserType.get(UserType_.OBJECTID), fromUser.get(User_.USERTYPE)),
                builder.equal(fromUserAccount.get(UserAccount_.OWNER), fromUser.get(User_.OBJECTID))
        ));

        List<UserAccount> results = objectService.getEm().createQuery(criteria)
                .getResultList();

        return results;
    }
    
    private String generateUserAPIKey(User user) {
        String key = user.getUSERTYPE().toString() + user.getOBJECTID() + user.getOBJECT_NAME() + user.getSTART_DATE() + user.getEND_DATE() + user.getVersion();
        key = EncryptionUtility.getHash(key, EncryptionType.SHA256);
        
        return key;
    }   
    
    /**
     * WARNING: This method is very unsecure. API keys should be hashed like passwords.
     * 
     * @param apiKey
     * @return 
     */
    public UserAccount getUserAccountByAPIKey(String apiKey) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<UserAccount> query = builder.createQuery(UserAccount.class);
        Root<UserAccount> fromUserAccount = query.from(UserAccount.class);
        
        query.select(fromUserAccount);
        query.where(builder.equal(fromUserAccount.get(UserAccount_.API_KEY), apiKey));
        
        List<UserAccount> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        if(results == null || results.isEmpty())
            return null;
        
        return results.get(0);
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserAccount regenerateAPIKey(long userId) {
        UserAccount account = this.getUserAccountById(userId);
        String apiKey = this.generateUserAPIKey(account.getOWNER());
        account.setAPI_KEY(apiKey);
        
        account = objectService.getEm().merge(account);
        
        return account;
    }
    
    /**
     * A very important design principle - each user should be identified by their 
     * email contact, not username. Username should only be just an unique attribute
     * but email should be primarily used to identify the person because it is an
     * external source.
     * 
     * @param email
     * @return 
     */
    public UserAccount getUserAccountByContactEmail(String email) {
        
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);
        Root<UserAccount> sourceEntity = criteria.from(UserAccount.class);

        criteria.select(sourceEntity);
        criteria.where(builder.equal(sourceEntity.get(UserAccount_.CONTACT_EMAIL), email)); 

        //Temporary measure before we find a better way to define the underlying
        //data of UserAccount object and subsequently how to retrieve the correct
        //result.
        List<UserAccount> results = objectService.getEm().createQuery(criteria)
                .getResultList();

        if (results == null || results.size() <= 0) {
            return null;
        }

        return results.get(0); 
    }
    
    /**
     * 1) Locks the account if it hasn't been locked (to prevent anymore logins other than the person who receives the reset email)
     * 2) Generate a PasswordResetRequest with TRANSACTION_KEY, along with the Trigger_Password_User link to User
     * 3) Sends an email to the account holder with a link /reset/[TRANSACTION_KEY]
     * 4) Returns the key
     * 
     * @param email
     * @return 
     * @throws eds.component.data.IncompleteDataException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String generatePasswordResetToken(String email) throws IncompleteDataException, EntityNotFoundException {
        //1)
        UserAccount acct = getUserAccountByContactEmail(email);
        if(acct == null)
            throw new EntityNotFoundException("Username not found.");
        
        acct.setUSER_LOCKED(true);
        acct = em.merge(acct);
        
        //2)
        PasswordResetRequest req = new PasswordResetRequest();
        req.setPROGRAM(PasswordResetRequest.class.getSimpleName());
        req.setPROCESSING_STATUS(PWD_PROCESSING_STATUS.NEW.label);
        em.persist(req);
        
        Trigger_Password_User trigger = new Trigger_Password_User();
        trigger.setTRIGGERED_TRANSACTION(req);
        trigger.setTRIGGERING_OBJECT(acct.getOWNER());
        em.persist(trigger);
        
        //3)
        ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.WEB);
        String message = "";
        message += "Hi,";
        message += "<br>";
        message += "<br>You have requested a password reset for your Segmail account.";
        message += "<br>Please click <a target=\"_blank\" href=\""+server.getURI()+"/reset/"+req.getTRANSACTION_KEY()+"\">here</a> to reset your password.";
        message += "<br>If you did not request this, please ignore this email.";
        message += "<br>";
        message += "<br>Regards,";
        message += "<br>Segmail Administrator";
        message += "<br>";
        message += "<br>Segmail - Pay for effective emails, not storage.";
        
        mailService.sendQuickMail(
                "Segmail Password Reset", 
                message, 
                ADMIN_EMAIL,
                DateTime.now(), 
                true,
                email);
        
        //4)
        String token = req.getTRANSACTION_KEY();
        return token;
        
    }
    
    /**
     * 
     * @param token
     * @param password
     * @throws TransactionNotFoundException if the token is invalid or not found
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void resetPassword(String token, String password) throws TransactionNotFoundException {
        //2 separate queries are better
        List<User> users = getUserByPasswordResetToken(token);
        if(users == null || users.isEmpty())
            throw new TransactionNotFoundException();
        
        UserAccount acct = this.getUserAccountById(users.get(0).getOBJECTID());
        
        PasswordResetRequest req = txService.getTransactionByKey(token, PasswordResetRequest.class);
        //If token is not NEW, also throw a TransactionNotFoundException
        if(!PWD_PROCESSING_STATUS.NEW.label.equalsIgnoreCase(req.getPROCESSING_STATUS()))
            throw new TransactionNotFoundException();
        
        //Set new password
        String hashedPassword = this.getPasswordHash(acct.getUSERNAME(), password, HASH_KEY);
        acct.setPASSWORD(hashedPassword);
        //Unlock account
        acct.setUSER_LOCKED(false);
        
        acct = em.merge(acct);
        
        //Update the req as PROCESSED
        req.setPROCESSING_STATUS(PWD_PROCESSING_STATUS.PROCESSED.label);
        
        req = em.merge(req);
    }
    
    public List<User> getUserByPasswordResetToken(String token) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<Trigger_Password_User> fromTg = query.from(Trigger_Password_User.class);
        Root<PasswordResetRequest> fromReq = query.from(PasswordResetRequest.class);
        Root<User> fromUser = query.from(User.class);
        
        query.select(fromUser);
        query.where(builder.and(
                builder.equal(fromReq.get(PasswordResetRequest_.TRANSACTION_KEY), token),
                builder.equal(fromReq.get(PasswordResetRequest_.TRANSACTION_ID), fromTg.get(Trigger_Password_User_.TRIGGERED_TRANSACTION)),
                builder.equal(fromTg.get(Trigger_Password_User_.TRIGGERING_OBJECT), fromUser.get(User_.OBJECTID))
        ));
        
        List<User> results = em.createQuery(query)
                .getResultList();
        
        return results;
    }
}
