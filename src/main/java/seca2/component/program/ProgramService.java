/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.program;

import java.io.Serializable;
import java.util.List;
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
import seca2.bootstrap.GlobalValues;
import seca2.component.data.DBConnectionException;
import seca2.entity.program.Program;
import seca2.entity.program.Program_;
import seca2.entity.user.UserType;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ProgramService implements Serializable {
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Program> getProgramByName(String programName) throws DBConnectionException {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Program> criteria = builder.createQuery(Program.class);
            Root<Program> sourceEntity = criteria.from(Program.class); //FROM UserType

            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(Program_.PROGRAM_NAME), programName));

            List<Program> results = em.createQuery(criteria)
                    .setFirstResult(0)
                    .setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB)
                    .getResultList();

            return results;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    //Translation services
    
    public String programToURI(String programName){
        return null;
    }
    
    public String URIToProgram(String URI){
        return null;
    }
}
