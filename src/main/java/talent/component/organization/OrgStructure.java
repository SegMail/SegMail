/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.component.organization;

import eds.component.GenericEnterpriseObjectService;
import java.util.Iterator;
import java.util.List;
import talent.entity.organization.OrgUnit;

/**
 * A simple preliminary design for the org structure, as we wait on for a more
 * complete and re-usable design based on EDS in general using evaluation path.
 * 
 * Incremental discovery: only when iterated to the next node, then the object is 
 * created. No mass reading of objects at this point.
 * 
 * Default evaluation path: OrgUnit->OrgUnit->Position->Employee
 * 
 * @author LeeKiatHaw
 */
public class OrgStructure implements Iterable<OrgUnit> {
    
    private GenericEnterpriseObjectService objectService;
    
    private OrgUnit root;
    
    public OrgStructure(OrgUnit rootUnit){
        
    }

    @Override
    public Iterator<OrgUnit> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
