package eds.component.layout;

import eds.component.GenericEnterpriseObjectService;
import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import eds.entity.EnterpriseObject;
import eds.entity.EnterpriseRelationship;
import eds.entity.client.Client;
import eds.entity.layout.Layout;
import eds.entity.layout.LayoutAssignment;
import eds.entity.layout.Layout_;
import eds.entity.user.User;
import eds.entity.user.UserType;
import java.io.Serializable;
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

            Layout layout = this.getLayoutById(layoutid);
            if (layout == null) {
                throw new LayoutAssignmentException("Layout id " + layoutid + " does not exist.");
            }

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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Layout getLayoutById(long layoutid)
            throws DBConnectionException {

        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Layout> criteria = builder.createQuery(Layout.class);
            Root<Layout> sourceEntity = criteria.from(Layout.class); //FROM Layout

            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(Layout_.OBJECTID), layoutid));

            List<Layout> results = em.createQuery(criteria)
                    .getResultList();

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
    public Layout getLayoutByName(String layoutName)
            throws DBConnectionException {

        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Layout> criteria = builder.createQuery(Layout.class);
            Root<Layout> sourceEntity = criteria.from(Layout.class); //FROM Layout

            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(Layout_.LAYOUT_NAME), layoutName));

            List<Layout> results = em.createQuery(criteria)
                    .getResultList();

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
    
    
}
