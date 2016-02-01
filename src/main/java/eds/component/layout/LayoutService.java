package eds.component.layout;

import eds.component.GenericObjectService;
import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.program.ProgramService;
import eds.component.user.UserService;
import eds.entity.data.EnterpriseObject;
import eds.entity.client.Client;
import eds.entity.data.EnterpriseObject_;
import eds.entity.layout.Layout;
import eds.entity.layout.LayoutAssignment;
import eds.entity.layout.LayoutAssignment_;
import eds.entity.layout.Layout_;
import eds.entity.program.Program;
import eds.entity.program.Program_;
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
    @EJB private ProgramService programService;
    @EJB private ClientService clientService;
    @EJB private GenericObjectService genericEOService;

    @PersistenceContext(name = "HIBERNATE") //When it's more stable, take this out
    //All custom EJB should get the EM from EDS
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
            

            //LayoutAssignment layoutAssignment1 = new LayoutAssignment();
            LayoutAssignment layoutAssignment2 = new LayoutAssignment(layout,user);

            //layoutAssignment1.setSOURCE(user);
            //layoutAssignment1.setTARGET(layout);

            //layoutAssignment2.setTARGET(user);
            //layoutAssignment2.setSOURCE(layout);

            //em.persist(layoutAssignment1);
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

            //LayoutAssignment layoutAssignment1 = new LayoutAssignment();
            LayoutAssignment layoutAssignment2 = new LayoutAssignment(layout,usertype);

            //layoutAssignment1.setSOURCE(usertype);
            //layoutAssignment1.setTARGET(layout);

            //layoutAssignment2.setTARGET(usertype);
            //layoutAssignment2.setSOURCE(layout);

            //em.persist(layoutAssignment1);
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
            //Try the new GenericObjectService!
            //Check if client exists
            Client clientEO = genericEOService.getEnterpriseObjectById(clientid,Client.class);
            if(clientEO == null)
                throw new LayoutAssignmentException("Client "+clientid+" does not exist.");
            
            //Check if client exists
            Layout layoutEO = genericEOService.getEnterpriseObjectById(layoutid,Layout.class);
            if(layoutEO == null)
                throw new LayoutAssignmentException("Layout "+layoutid+" does not exist.");
            
            //Check if the assignment already exists
            //List<LayoutAssignment> existingRels1 = genericEOService.getRelationshipsForSourceObject(layoutid, LayoutAssignment.class);
            List<LayoutAssignment> existingRels2 = genericEOService.getRelationshipsForTargetObject(clientid, LayoutAssignment.class);
            
            if(existingRels2.size() > 0)
                throw new LayoutAssignmentException("Assignment already exists!");
            
            //If all validations are passed, create the bidirectional relationship
            LayoutAssignment layoutAssignment1 = new LayoutAssignment(layoutEO,clientEO);
            //LayoutAssignment layoutAssignment2 = new LayoutAssignment();

            //layoutAssignment1.setSOURCE(layoutEO);
            //layoutAssignment1.setTARGET(clientEO);

            //layoutAssignment2.setTARGET(clientEO);
            //layoutAssignment2.setSOURCE(layoutEO);

            em.persist(layoutAssignment1);
            //em.persist(layoutAssignment2);
            
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
     * @throws eds.component.data.EntityNotFoundException
     * @throws eds.component.data.RelationshipExistsException
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignLayout(long objectid, long layoutid)
            throws EntityNotFoundException, RelationshipExistsException {
        try {
            
            //Check if layout exists
            Layout layoutEO = this.getLayoutById(layoutid);
            if(layoutEO == null)
                //throw new LayoutAssignmentException("Layout "+layoutid+" does not exist.");
                throw new EntityNotFoundException(Layout.class,layoutid);
            
            //Check if target exists
            EnterpriseObject clientEO = genericEOService.getEnterpriseObjectById(objectid);
            if(clientEO == null)
                //throw new LayoutAssignmentException("Object "+objectid+" does not exist.");
                throw new EntityNotFoundException(EnterpriseObject.class,objectid);
            
            LayoutAssignment layoutAssignment2 = new LayoutAssignment(layoutEO,clientEO);
            //Check if the assignment already exists
            //List<LayoutAssignment> existingRels1 = genericEOService.getRelationshipsForSourceObject(objectid,LayoutAssignment.class);
            List<LayoutAssignment> existingRels2 = genericEOService.getRelationshipsForTargetObject(objectid,LayoutAssignment.class);
            
            if(existingRels2 != null && existingRels2.size() > 0)
                //throw new LayoutAssignmentException("Assignment already exists!");
                throw new RelationshipExistsException(layoutAssignment2);
            
            em.persist(layoutAssignment2);
            

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Layout> getLayoutsByProgram(String programName)
        throws DBConnectionException {
        try{
            
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Layout> query = builder.createQuery(Layout.class);
            Root<LayoutAssignment> fromAssign = query.from(LayoutAssignment.class);
            Root<Program> fromProgram = query.from(Program.class);
            Root<Layout> fromLayout = query.from(Layout.class);
            
            query.select(fromLayout);
            query.where(
                builder.and(
                        builder.equal(fromAssign.get(LayoutAssignment_.SOURCE), fromLayout.get(Layout_.OBJECTID)),
                        builder.equal(fromAssign.get(LayoutAssignment_.TARGET), fromProgram.get(Program_.OBJECTID)),
                        builder.equal(fromProgram.get(Program_.PROGRAM_NAME), programName)
                )
            );
            
            List<Layout> results = em.createQuery(query)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
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
    public List<Layout> getLayoutsByUser(User user) {
        try{
            if(user == null)
                return new ArrayList<Layout>();
            //To optimize, pull out all assignments for User, UserType and Client
            UserType userType = user.getUSERTYPE();
            //I'm not writing this in GenericObjectService yet because
            //this is the only method using such a retrieval technique.
            List<Long> IDs = new ArrayList<Long>();
            IDs.add(user.getOBJECTID());
            
            if(user.getUSERTYPE() != null) IDs.add(user.getUSERTYPE().getOBJECTID());
            
            List<Layout> results = this.genericEOService.getAllSourceObjectsFromTargets(IDs, LayoutAssignment.class, Layout.class);
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
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
