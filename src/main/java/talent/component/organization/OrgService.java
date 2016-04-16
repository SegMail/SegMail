/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.component.organization;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.entity.client.Client;
import eds.entity.data.EnterpriseRelationship;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;
import talent.entity.organization.BelongsTo;
import talent.entity.organization.OrgUnit;
import talent.entity.organization.BusinessUnitClientAssignment;
import talent.entity.organization.ManagerAssignment;
import talent.entity.organization.Position;
import talent.entity.organization.EmployeeAssignment;
import talent.entity.organization.ExistUnder;
import talent.entity.talent.Employee;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class OrgService {

    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;

    @EJB
    private GenericObjectService objectService;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<OrgUnit> getBusinessUnitsForClientId(long clientid) {
        try {
            List<OrgUnit> results = objectService.getAllTargetObjectsFromSource(clientid, BusinessUnitClientAssignment.class, OrgUnit.class);
            return results;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    /**
     * Assumes that each BU has only 1 parent
     * 
     * @param buid
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public OrgUnit getParentForBU(long buid) {
        try {
            List<OrgUnit> results = this.objectService.getAllTargetObjectsFromSource(buid, BelongsTo.class, OrgUnit.class);
            
            if(results == null || results.isEmpty())
                return null;
            return results.get(0); // Only returns the 1st result

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BelongsTo> getBelongsToForBUs(List<OrgUnit> bus){
        try {
            List<Long> ids = new ArrayList<Long>();
            for(OrgUnit bu : bus){
                ids.add(bu.getOBJECTID());
            }
            List<BelongsTo> results = objectService.getRelationshipsForSourceObjects(ids, BelongsTo.class);
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public OrgUnit createNewOrg(String orgName, long clientid) throws EntityExistsException, EntityNotFoundException{
        try {
            List<OrgUnit> existingBUs = objectService.getEnterpriseObjectsByName(orgName, OrgUnit.class);
            if(existingBUs != null && !existingBUs.isEmpty())
                throw new EntityExistsException("Business Unit "+orgName+" already exist!");
            
            Client client = objectService.getEnterpriseObjectById(clientid, Client.class);
            if(client == null)
                throw new EntityNotFoundException(Client.class,clientid);
            
            OrgUnit bu = new OrgUnit();
            bu.setUNIT_NAME(orgName);
            
            em.persist(bu);
            
            //Create the assignment
            BusinessUnitClientAssignment clientAssignment = new BusinessUnitClientAssignment();
            clientAssignment.setSOURCE(client);
            clientAssignment.setTARGET(bu);
            
            em.persist(clientAssignment);
            
            return bu;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    /**
     * Not going to be used now since we are only creating manager assignments.
     * 
     * @param posName
     * @param orgunitid
     * @return
     * @throws EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ExistUnder createNewPositionUnderOrgUnit(String posName, long orgunitid) 
            throws EntityNotFoundException{
        try {
            // Get the BU
            OrgUnit bu = objectService.getEnterpriseObjectById(orgunitid, OrgUnit.class);
            if(bu == null)
                throw new EntityNotFoundException("OrgUnit "+orgunitid+" not found.");
            
            // Create the Position
            Position newPos = new Position();
            newPos.setJOB_TITLE(posName);
            
            em.persist(newPos);
            
            ExistUnder existUnder = new ExistUnder();
            existUnder.setTARGET(bu);
            existUnder.setSOURCE(newPos);
            
            em.persist(existUnder);
            
            return existUnder;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    /**
     * As of now we are only going to create a ManagerAssignment relationship between
 Position and OrgUnit. Our TM app is supposed to be used for key appointment
     * holders only.
     * 
     * @param posName
     * @param orgId
     * @return
     * @throws EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ManagerAssignment createManagerPosition(String posName, long orgId) 
            throws EntityNotFoundException, EntityExistsException{
        try {
            OrgUnit existingBU = objectService.getEnterpriseObjectById(orgId, OrgUnit.class);
            if(existingBU == null)
                throw new EntityNotFoundException("Org unit "+orgId+" not found.");
            
            List<Position> existingPos = objectService.getEnterpriseObjectsByName(posName, Position.class);
            if(existingPos != null && !existingPos.isEmpty())
                throw new EntityExistsException(existingPos.get(0));
            
            //Create position object first
            Position newPos = new Position();
            newPos.setJOB_TITLE(posName);
            em.persist(newPos);
            
            //Create ManagerAssignment
            ManagerAssignment assignment = new ManagerAssignment();
            assignment.setSOURCE(newPos);
            assignment.setTARGET(existingBU);
            em.persist(existingBU);
            
            return assignment;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmployeeAssignment createRoleAssignment(long positionId, long employeeId) throws EntityNotFoundException{
        try {
            Employee emp = objectService.getEnterpriseObjectById(employeeId, Employee.class);
            if(emp == null)
                throw new EntityNotFoundException("Employee "+employeeId+" does not exist.");
            
            Position pos = objectService.getEnterpriseObjectById(positionId, Position.class);
            if(pos == null)
                throw new EntityNotFoundException("Position "+positionId+" does not exist.");
            
            EmployeeAssignment newAssign = new EmployeeAssignment();
            newAssign.setSOURCE(emp);
            newAssign.setTARGET(pos);
            em.persist(newAssign);
            
            return newAssign;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    } 
    
    /**
     * 
     * @param orgName
     * @param posName
     * @param empId Optional - If 0 or -1 then no employee will be assigned.
     * @return a list of newly created EmployeeAssignment and ManagerAssignment objects
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<EnterpriseRelationship> createNewOrgPos(String orgName, String posName, long empId, long clientid) 
            throws EntityExistsException, EntityNotFoundException{
        try {
            List<EnterpriseRelationship> results = new ArrayList<EnterpriseRelationship>();
            //1. Create the org unit first
            OrgUnit newBU = this.createNewOrg(orgName, clientid);
            
            //2. Create the position and assign to the org unit
            ManagerAssignment pAssignment = this.createManagerPosition(posName, newBU.getOBJECTID());
            results.add(pAssignment);
            
            //3. Assign the employee
            
            if(empId > 0){
                Position pos = pAssignment.getSOURCE();
                EmployeeAssignment empAssignment = this.assignEmpToPosition(empId, pos.getOBJECTID());
                results.add(empAssignment);
            }
                
            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmployeeAssignment assignEmpToPosition(long empId, long posId) 
            throws EntityNotFoundException{
        try {
            Employee emp = objectService.getEnterpriseObjectById(empId, Employee.class);
            if(emp == null)
                throw new EntityNotFoundException(Employee.class,empId);
            
            Position pos = objectService.getEnterpriseObjectById(posId, Position.class);
            if(pos == null)
                throw new EntityNotFoundException(Position.class,posId);
            
            EmployeeAssignment newAssignment = new EmployeeAssignment();
            newAssignment.setSOURCE(emp);
            newAssignment.setTARGET(pos);
            
            return newAssignment;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
}
