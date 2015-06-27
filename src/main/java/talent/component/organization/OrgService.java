/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.component.organization;

import eds.component.GenericEnterpriseObjectService;
import eds.component.data.DBConnectionException;
import eds.entity.client.ClientAssignment;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;
import talent.entity.organization.BusinessUnit;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class OrgService {
    
    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;
    
    @EJB private GenericEnterpriseObjectService objectService;
    
    public List<BusinessUnit> getBusinessUnitsForClientId(long clientid){
        try {
            List<BusinessUnit> results = objectService.getAllTargetObjectsFromSource(clientid, ClientAssignment.class, BusinessUnit.class);
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
}
