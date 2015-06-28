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
import eds.entity.client.ClientResourceAssignment;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;
import talent.entity.organization.BelongsTo;
import talent.entity.organization.BusinessUnit;

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
    
    
    //Build an org chart!
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

}
