/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.subscription;

import eds.component.GenericEnterpriseObjectService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class SubscriptionService {
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;
    
    @EJB private GenericEnterpriseObjectService genericEntepriseObjectService;
    
    
}
