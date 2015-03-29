package eds.component.layout;

import eds.component.GenericEnterpriseObjectService;
import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import eds.entity.EnterpriseObject;
import eds.entity.EnterpriseRelationship;
import eds.entity.EnterpriseRelationship_;
import eds.entity.client.Client;
import eds.entity.layout.Layout;
import eds.entity.layout.LayoutAssignment;
import eds.entity.layout.LayoutAssignment_;
import eds.entity.user.User;
import eds.entity.user.UserType;
import java.io.Serializable;
import java.util.ArrayList;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class LayoutService implements Serializable {

    @EJB private UserService userService;
    @EJB private ClientService clientService;
    @EJB private GenericEnterpriseObjectService genericEOService;

    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerLayout(String layoutName, String viewRoot)
            throws DBConnectionException, LayoutRegistrationException {

        try {
            
            //Check if layoutName is empty
            if (layoutName == null || layoutName.length() <= 0) {
                throw new LayoutRegistrationException("Layout name cannot be empty.");
            }
            
            //Check if viewRoot is empty
            if (viewRoot == null || viewRoot.length() <= 0) {
                throw new LayoutRegistrationException("View root cannot be empty.");
            }
            
            //Check if layoutName is already used
            if (this.getLayoutByName(layoutName) != null){
                throw new LayoutRegistrationException("Layout "+layoutName+" already exists.");
            }

            Layout newLayout = new Layout();
            newLayout.setLAYOUT_NAME(layoutName);
            newLayout.setVIEW_ROOT(viewRoot);

            em.persist(newLayout);

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
    public void assignLayoutToUser(long userid, long layoutid)
            throws DBConnectionException, LayoutAssignmentException {

        try {
            //Check if user exists
            User user = userService.getUserById(userid);
            if (user == null) {
                throw new LayoutAssignmentException("User id " + userid + " does not exist.");
            }

            //Check if layout exists
            Layout layout = this.getLayoutById(layoutid);
            if (layout == null) {
                throw new LayoutAssignmentException("Layout id " + layoutid + " does not exist.");
            }
            
            //Check if the assignment already exists
            List<LayoutAssignment> existingRels1 = genericEOService.getRelationshipsForSourceObject(userid,LayoutAssignment.class);
            List<LayoutAssignment> existingRels2 = genericEOService.getRelationshipsForTargetObject(userid,LayoutAssignment.class);
            
            if(existingRels1.size() > 0 || existingRels2.size() > 0)
                throw new LayoutAssignmentException("A Layout is already assigned to this UserType!");
            

            LayoutAssignment layoutAssignment1 = new LayoutAssignment();
            LayoutAssignment layoutAssignment2 = new LayoutAssignment();

            layoutAssignment1.setSOURCE(user);
            layoutAssignment1.setTARGET(layout);

            layoutAssignment2.setTARGET(user);
            layoutAssignment2.setSOURCE(layout);

            em.persist(layoutAssignment1);
            em.persist(layoutAssignment2);

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
    public void assignLayoutToUserType(long usertypeid, long layoutid)
            throws DBConnectionException, LayoutAssignmentException {

        try {
            //Check if user exists
            UserType usertype = userService.getUserTypeById(usertypeid);
            if (usertype == null) {
                throw new LayoutAssignmentException("Usertype id " + usertypeid + " does not exist.");
            }

            Layout layout = this.getLayoutById(layoutid);
            if (layout == null) {
                throw new LayoutAssignmentException("Layout id " + layoutid + " does not exist.");
            }
            
            //Check if the assignment already exists
            List<LayoutAssignment> existingRels1 = genericEOService.getRelationshipsForSourceObject(usertypeid,LayoutAssignment.class);
            List<LayoutAssignment> existingRels2 = genericEOService.getRelationshipsForTargetObject(usertypeid,LayoutAssignment.class);
            
            if(existingRels1.size() > 0 || existingRels2.size() > 0)
                throw new LayoutAssignmentException("A Layout is already assigned to this UserType!");

            LayoutAssignment layoutAssignment1 = new LayoutAssignment();
            LayoutAssignment layoutAssignment2 = new LayoutAssignment();

            layoutAssignment1.setSOURCE(usertype);
            layoutAssignment1.setTARGET(layout);

            layoutAssignment2.setTARGET(usertype);
            layoutAssignment2.setSOURCE(layout);

            em.persist(layoutAssignment1);
            em.persist(layoutAssignment2);

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
    public void assignLayoutToClient(long clientid, long layoutid)
            throws DBConnectionException, LayoutAssignmentException {

        try {
            //Try the new GenericEnterpriseObjectService!
            //Check if client exists
            EnterpriseObject clientEO = genericEOService.getEnterpriseObjectById(clientid);
            if(clientEO == null || !(clientEO instanceof Client))
                throw new LayoutAssignmentException("Client "+clientid+" does not exist.");
            
            //Check if client exists
            EnterpriseObject layoutEO = genericEOService.getEnterpriseObjectById(layoutid);
            if(layoutEO == null || !(layoutEO instanceof Layout))
                throw new LayoutAssignmentException("Layout "+layoutid+" does not exist.");
            
            //Check if the assignment already exists
            List<EnterpriseRelationship> existingRels1 = genericEOService.getRelationshipsForObjects(clientid, layoutid);
            List<EnterpriseRelationship> existingRels2 = genericEOService.getRelationshipsForObjects(layoutid, clientid);
            
            if(existingRels1.size() > 0 || existingRels2.size() > 0)
                throw new LayoutAssignmentException("Assignment already exists!");
            
            //If all validations are passed, create the bidirectional relationship
            LayoutAssignment layoutAssignment1 = new LayoutAssignment();
            LayoutAssignment layoutAssignment2 = new LayoutAssignment();

            layoutAssignment1.setSOURCE(clientEO);
            layoutAssignment1.setTARGET(layoutEO);

            layoutAssignment2.setTARGET(clientEO);
            layoutAssignment2.setSOURCE(layoutEO);

            em.persist(layoutAssignment1);
            em.persist(layoutAssignment2);
            

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
     * Decided not to restrict the assigned object type.
     * 
     * @param layoutid
     * @param objectid
     * @throws DBConnectionException
     * @throws LayoutAssignmentException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignLayout(long layoutid, long objectid)
            throws DBConnectionException, LayoutAssignmentException {
        try {
            
            //Check if layout exists
            Layout layoutEO = this.getLayoutById(layoutid);
            if(layoutEO == null)
                throw new LayoutAssignmentException("Layout "+layoutid+" does not exist.");
            
            //Check if target exists
            EnterpriseObject clientEO = genericEOService.getEnterpriseObjectById(objectid);
            if(clientEO == null)
                throw new LayoutAssignmentException("Object "+objectid+" does not exist.");
            
            //Check if the assignment already exists
            List<EnterpriseRelationship> existingRels1 = genericEOService.getRelationshipsForObjects(objectid, layoutid);
            List<EnterpriseRelationship> existingRels2 = genericEOService.getRelationshipsForObjects(layoutid, objectid);
            
            if(existingRels1.size() > 0 || existingRels2.size() > 0)
                throw new LayoutAssignmentException("Assignment already exists!");
            
            //If all validations are passed, create the bidirectional relationship
            LayoutAssignment layoutAssignment1 = new LayoutAssignment();
            LayoutAssignment layoutAssignment2 = new LayoutAssignment();

            layoutAssignment1.setSOURCE(clientEO);
            layoutAssignment1.setTARGET(layoutEO);

            layoutAssignment2.setTARGET(clientEO);
            layoutAssignment2.setSOURCE(layoutEO);

            em.persist(layoutAssignment1);
            em.persist(layoutAssignment2);
            

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
    public Layout getLayoutById(long layoutid)
            throws DBConnectionException {

        try {
            /*CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Layout> criteria = builder.createQuery(Layout.class);
            Root<Layout> sourceEntity = criteria.from(Layout.class); //FROM Layout

            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(Layout_.OBJECTID), layoutid));

            List<Layout> results = em.createQuery(criteria)
                    .getResultList();

            return (results.size() > 0) ? results.get(0) : null;*/
            
            return this.genericEOService.getEnterpriseObjectById(layoutid, Layout.class);

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
    public Layout getLayoutByName(String layoutName)
            throws DBConnectionException {

        try {
            /*CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Layout> criteria = builder.createQuery(Layout.class);
            Root<Layout> sourceEntity = criteria.from(Layout.class); //FROM Layout

            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(Layout_.LAYOUT_NAME), layoutName));

            List<Layout> results = em.createQuery(criteria)
                    .getResultList();*/
            
            List<Layout> results = this.genericEOService.getEnterpriseObjectsByName(layoutName, Layout.class);

            return (results.size() > 0) ? results.get(0) : null;
            

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
    public String getLayoutViewRootByName(String layoutName)
        throws DBConnectionException {
        return this.getLayoutByName(layoutName).getVIEW_ROOT();
    }
    
    public String getLayoutViewRootForSite(){
        throw new UnsupportedOperationException();
    }
    
    /**
     * If there is a assignment for that particular user, return it. 
     * Else, look for any assignments for the User's UserType.
     * Else, look for any assignments within the client. [de-prioritized]
     * Else, return a null object.
     * 
     * @param userid
     * @return
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Layout getLayoutByUserAssignment(User user)
        throws DBConnectionException {
        try{
            //To optimize, pull out all assignments for User, UserType and Client
            UserType userType = user.getUSERTYPE();
            //I'm not writing this in GenericEnterpriseObjectService yet because
            //this is the only method using such a retrieval technique.
            List<Long> IDs = new ArrayList<Long>();
            IDs.add(user.getOBJECTID());
            IDs.add(user.getUSERTYPE().getOBJECTID());
            //Add the client object ID
            
            //Construct the query
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<LayoutAssignment> criteria = builder.createQuery(LayoutAssignment.class);
            Root<LayoutAssignment> sourceEntity = criteria.from(LayoutAssignment.class); //FROM Layout

            criteria.select(sourceEntity);
            criteria.where(sourceEntity.get(LayoutAssignment_.SOURCE).in(IDs));
            
            List<LayoutAssignment> results = em.createQuery(criteria).getResultList();
            
            //Looping shouldn't cause NullPointerExceptions
            //Look for User first
            for(LayoutAssignment result:results){
                EnterpriseObject source = result.getSOURCE();
                if(source instanceof User)
                    return (Layout) result.getTARGET();
            }
            //Then look for UserType
            for(LayoutAssignment result:results){
                EnterpriseObject source = result.getSOURCE();
                if(source instanceof UserType)
                    return (Layout) result.getTARGET();
            }
            
            //Then look for Client
            
            return null;
            
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
    public Layout getLayoutByUsernameAssignment(String username)
        throws DBConnectionException {
        
        try{
            User user = (User) this.userService.getUserAccountByUsername(username).getOWNER();
            
            return this.getLayoutByUserAssignment(user);
            
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
    public List<Layout> getAllLayouts() throws DBConnectionException{
        try{
            return this.genericEOService.getAllEnterpriseObjects(Layout.class);
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
