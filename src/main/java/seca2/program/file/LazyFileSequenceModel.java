/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.file;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import eds.component.data.HibernateUtil;
import eds.entity.file.SecaFileSequence;
import javax.ejb.EJB;

/**
 *
 * @author vincent.a.lee
 */
public class LazyFileSequenceModel extends LazyDataModel<SecaFileSequence>{

    @EJB private HibernateUtil hibernateUtil;
    
    @Override
    public List<SecaFileSequence> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        
        
        return super.load(first, pageSize, sortField, sortOrder, filters); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
