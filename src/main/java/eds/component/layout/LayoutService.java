package eds.component.layout;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.user.UserService;
import eds.entity.data.EnterpriseObject;
import eds.entity.client.Client;
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

    @EJB
    private UserService userService;
    @EJB
    private GenericObjectService objectService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerLayout(String layoutName, String viewRoot)
            throws EntityExistsException, IncompleteDataException {

        try {

            //Check if layoutName is empty
            if (layoutName == null || layoutName.length() <= 0) {
                throw new IncompleteDataException("Layout name cannot be empty.");
            }

            //Check if viewRoot is empty
            if (viewRoot == null || viewRoot.length() <= 0) {
                throw new IncompleteDataException("View root cannot be empty.");
            }

            //Check if layoutName is already used
            Layout existingLayout = getLayoutByName(layoutName);
            if (existingLayout != null) {
                throw new EntityExistsException(existingLayout);
            }

            Layout newLayout = new Layout();
            newLayout.setLAYOUT_NAME(layoutName);
            newLayout.setVIEW_ROOT(viewRoot);

            objectService.getEm().persist(newLayout);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignLayoutToUser(long userid, long layoutid)
            throws RelationshipExistsException, EntityNotFoundException {

        try {
            //Check if user exists
            User user = userService.getUserById(userid);
            if (user == null) {
                throw new EntityNotFoundException(User.class, userid);
            }

            //Check if layout exists
            Layout layout = getLayoutById(layoutid);
            if (layout == null) {
                throw new EntityNotFoundException(Layout.class, layoutid);
            }

            //Check if the assignment already exists
            List<LayoutAssignment> existingRels1 = objectService.getRelationshipsForSourceObject(userid, LayoutAssignment.class);
            List<LayoutAssignment> existingRels2 = objectService.getRelationshipsForTargetObject(userid, LayoutAssignment.class);

            if (existingRels1.size() > 0 || existingRels2.size() > 0) {
                throw new RelationshipExistsException(existingRels2.get(0));
            }

            LayoutAssignment layoutAssignment2 = new LayoutAssignment(layout, user);

            objectService.getEm().persist(layoutAssignment2);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignLayoutToUserType(long usertypeid, long layoutid)
            throws RelationshipExistsException, EntityNotFoundException {

        try {
            //Check if user exists
            UserType usertype = userService.getUserTypeById(usertypeid);
            if (usertype == null) {
                throw new EntityNotFoundException(UserType.class, usertypeid);
            }

            Layout layout = getLayoutById(layoutid);
            if (layout == null) {
                throw new EntityNotFoundException(Layout.class, layoutid);
            }

            //Check if the assignment already exists
            List<LayoutAssignment> existingRels2 = objectService.getRelationshipsForTargetObject(usertypeid, LayoutAssignment.class);

            if (existingRels2 != null && existingRels2.size() > 0) {
                throw new RelationshipExistsException(existingRels2.get(0));
            }

            //LayoutAssignment layoutAssignment1 = new LayoutAssignment();
            LayoutAssignment layoutAssignment2 = new LayoutAssignment(layout, usertype);

            objectService.getEm().persist(layoutAssignment2);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignLayoutToClient(long clientid, long layoutid)
            throws EntityNotFoundException, RelationshipExistsException {

        try {
            //Try the new GenericObjectService!
            //Check if client exists
            Client clientEO = objectService.getEnterpriseObjectById(clientid, Client.class);
            if (clientEO == null) {
                throw new EntityNotFoundException(Client.class, clientid);
            }

            //Check if client exists
            Layout layoutEO = objectService.getEnterpriseObjectById(layoutid, Layout.class);
            if (layoutEO == null) {
                throw new EntityNotFoundException(Layout.class, layoutid);
            }

            //Check if the assignment already exists
            //List<LayoutAssignment> existingRels1 = objectService.getRelationshipsForSourceObject(layoutid, LayoutAssignment.class);
            List<LayoutAssignment> existingRels2 = objectService.getRelationshipsForTargetObject(clientid, LayoutAssignment.class);

            if (existingRels2 != null && existingRels2.size() > 0) {
                throw new RelationshipExistsException(existingRels2.get(0));
            }

            //If all validations are passed, create the bidirectional relationship
            LayoutAssignment layoutAssignment1 = new LayoutAssignment(layoutEO, clientEO);

            objectService.getEm().persist(layoutAssignment1);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    /**
     * Decided not to restrict the assigned object type.
     *
     * @param layoutid
     * @param objectid
     * @throws eds.component.data.EntityNotFoundException
     * @throws eds.component.data.RelationshipExistsException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignLayout(long objectid, long layoutid)
            throws EntityNotFoundException, RelationshipExistsException {
        try {

            //Check if layout exists
            Layout layoutEO = getLayoutById(layoutid);
            if (layoutEO == null) {
                throw new EntityNotFoundException(Layout.class, layoutid);
            }

            //Check if target exists
            EnterpriseObject clientEO = objectService.getEnterpriseObjectById(objectid);
            if (clientEO == null) {
                throw new EntityNotFoundException(EnterpriseObject.class, objectid);
            }

            LayoutAssignment layoutAssignment2 = new LayoutAssignment(layoutEO, clientEO);
            //Check if the assignment already exists
            //List<LayoutAssignment> existingRels1 = objectService.getRelationshipsForSourceObject(objectid,LayoutAssignment.class);
            List<LayoutAssignment> existingRels2 = objectService.getRelationshipsForTargetObject(objectid, LayoutAssignment.class);

            if (existingRels2 != null && existingRels2.size() > 0) {
                throw new RelationshipExistsException(layoutAssignment2);
            }

            objectService.getEm().persist(layoutAssignment2);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Layout getLayoutById(long layoutid) {

        try {
            return objectService.getEnterpriseObjectById(layoutid, Layout.class);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Layout getLayoutByName(String layoutName) {

        try {

            List<Layout> results = objectService.getEnterpriseObjectsByName(layoutName, Layout.class);

            return (results.size() > 0) ? results.get(0) : null;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getLayoutViewRootByName(String layoutName) {
        return getLayoutByName(layoutName).getVIEW_ROOT();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Layout> getLayoutsByProgram(String programName) {
        try {

            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
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

            List<Layout> results = objectService.getEm().createQuery(query)
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
     * If there is a assignment for that particular user, return it. Else, look
     * for any assignments for the User's UserType. Else, look for any
     * assignments within the client. [de-prioritized] Else, return a null
     * object.
     *
     * @param userid
     * @return
     * @
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Layout> getLayoutsByUserOrType(User user) {
        try {
            if (user == null) {
                return new ArrayList<Layout>();
            }
            //To optimize, pull out all assignments for User, UserType and Client
            UserType userType = user.getUSERTYPE();
            //I'm not writing this in GenericObjectService yet because
            //this is the only method using such a retrieval technique.
            List<Long> IDs = new ArrayList<Long>();
            IDs.add(user.getOBJECTID());

            if (user.getUSERTYPE() != null) {
                IDs.add(user.getUSERTYPE().getOBJECTID());
            }

            List<Layout> results = objectService.getAllSourceObjectsFromTargets(IDs, LayoutAssignment.class, Layout.class);

            return results;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Layout> getAllLayouts() {
        try {
            return objectService.getAllEnterpriseObjects(Layout.class);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

}
