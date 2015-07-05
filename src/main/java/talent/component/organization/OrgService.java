/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.component.organization;

import MapAPI.EntityMap;
import MapAPI.Node;
import eds.component.GenericEnterpriseObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.entity.client.ClientResourceAssignment;
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
import talent.entity.organization.BusinessUnit;
import talent.entity.organization.ManagerAssignment;
import talent.entity.organization.Position;
import talent.entity.organization.EmployeeAssignment;
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
    private GenericEnterpriseObjectService objectService;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BusinessUnit> getBusinessUnitsForClientId(long clientid) {
        try {
            List<BusinessUnit> results = objectService.getAllTargetObjectsFromSource(clientid, ClientResourceAssignment.class, BusinessUnit.class);
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
    public BusinessUnit getParentForBU(long buid) {
        try {
            List<BusinessUnit> results = this.objectService.getAllTargetObjectsFromSource(buid, BelongsTo.class, BusinessUnit.class);
            
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
    public List<BelongsTo> getBelongsToForBUs(List<BusinessUnit> bus){
        try {
            List<Long> ids = new ArrayList<Long>();
            for(BusinessUnit bu : bus){
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
    
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EntityMap<BusinessUnit,BelongsTo> buildOrgChartForClient(long clientid){
        try {
            // 1. Get all BusinessUnits for the client
            List<BusinessUnit> buList = this.getBusinessUnitsForClientId(clientid);
            
            // 2. Build nodes using the MapAPI
            EntityMap<BusinessUnit,BelongsTo> map = new EntityMap(buList,BelongsTo.class);
            
            return map;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BusinessUnit createNewOrg(String orgName) throws EntityExistsException{
        try {
            List<BusinessUnit> existingBUs = objectService.getEnterpriseObjectsByName(orgName, BusinessUnit.class);
            if(existingBUs != null && existingBUs.isEmpty())
                throw new EntityExistsException("Business Unit "+orgName+" already exist!");
            
            BusinessUnit bu = new BusinessUnit();
            bu.setUNIT_NAME(orgName);
            
            em.persist(bu);
            
            return bu;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
    
    /**
     * As of now we are only going to create a ManagerAssignment relationship between
     * Position and BusinessUnit. Our TM app is supposed to be used for key appointment
     * holders only.
     * 
     * @param posName
     * @param orgId
     * @return
     * @throws EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ManagerAssignment createManagerPosition(String posName, long orgId) throws EntityNotFoundException{
        try {
            BusinessUnit existingBU = objectService.getEnterpriseObjectById(orgId, BusinessUnit.class);
            if(existingBU == null)
                throw new EntityNotFoundException("Business unit "+orgId+" not found.");
            
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
     * @param empId
     * @return a list of newly created EmployeeAssignment and ManagerAssignment objects
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<EnterpriseRelationship> createNewOrgPos(String orgName, String posName, long empId, long clientid){
        try {
            //1. Create the org unit first
            return null;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }
}
