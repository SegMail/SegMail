/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.program;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import seca2.component.data.DBConnectionException;
import seca2.component.user.UserService;
import seca2.entity.program.Program;
import seca2.entity.program.ProgramAccess;
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
    
    @EJB private UserService userService;
    /**
     * 
     * @param programName
     * @return
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Program> getProgramByName(String programName) throws DBConnectionException {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Program> criteria = builder.createQuery(Program.class);
            Root<Program> sourceEntity = criteria.from(Program.class); //FROM UserType

            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(Program_.PROGRAM_NAME), programName));

            List<Program> results = em.createQuery(criteria)
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
    
    /**
     * 
     * @return
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Program> getAllPrograms() throws DBConnectionException {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Program> criteria = builder.createQuery(Program.class);
            Root<Program> sourceEntity = criteria.from(Program.class); //FROM UserType

            criteria.select(sourceEntity); // SELECT *

            List<Program> results = em.createQuery(criteria)
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignProgramToUserType(long programId, long userTypeId) 
            throws DBConnectionException, ProgramAssignmentException{
        try{
            //Get user type first
            UserType userType = userService.getUserTypeById(userTypeId);
            if(userType == null)
                throw new ProgramAssignmentException("Usertype with ID "+userTypeId+" does not exist!");

            //Get the program object
            Program program = this.getProgramById(programId);
            if(program == null)
                throw new ProgramAssignmentException("Program with ID "+programId+" does not exist!");

            //Check if the assignment already exist
            
            
            //Create bidrectional relationships
            ProgramAccess programAccess1 = new ProgramAccess();
            programAccess1.setSOURCE(program);
            programAccess1.setTARGET(userType);

            ProgramAccess programAccess2 = new ProgramAccess();
            programAccess2.setSOURCE(userType);
            programAccess2.setTARGET(program);

            em.persist(programAccess1);
            em.persist(programAccess2);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
        
    }
    /**
     * 
     * @param programName
     * @param viewDir
     * @param viewRoot
     * @param beanDir
     * @throws DBConnectionException 
     * @throws seca2.component.program.ProgramRegistrationException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerProgram(String programName, String viewDir, String viewRoot, String beanDir)
            throws DBConnectionException, ProgramRegistrationException {
        try{
            //Check if program name already exist
            List<Program> existingPrograms = this.getProgramByName(programName);
            if(existingPrograms != null && existingPrograms.size() >0)
                throw new ProgramRegistrationException("Program "+programName+" already exists!");
            
            Program program = new Program();
            
            //Set basic variables
            program.setPROGRAM_NAME(programName);
            program.setVIEW_DIRECTORY(viewDir);
            program.setVIEW_ROOT(viewRoot);
            program.setBEAN_DIRECTORY(beanDir);
            
            //Set EnterpriseObject properties
            program.setOBJECT_NAME(programName);
            
            em.persist(program);
            
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
    public Program getProgramById(long programId)
            throws DBConnectionException{
        try{
            /*
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Program> criteria = builder.createQuery(Program.class);
            Root<Program> sourceEntity = criteria.from(Program.class); //FROM UserType

            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(Program_.OBJECTID), programId));

            List<Program> results = em.createQuery(criteria)
                    .getResultList();
            if(results == null || results.size() <= 0)
                return null;
            
            return results.get(0); //return only the first result
            */
            
            //try this
            return em.find(Program.class, programId);
            
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
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getViewRootFromProgramName(String programName)
        throws DBConnectionException{
        try{
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> criteria = builder.createTupleQuery();
            Root<Program> sourceEntity = criteria.from(Program.class); //FROM UserType

            criteria.multiselect(
                    sourceEntity.get(Program_.VIEW_DIRECTORY),
                    sourceEntity.get(Program_.VIEW_ROOT)); // SELECT *
            
            criteria.where(builder.equal(sourceEntity.get(Program_.PROGRAM_NAME), programName)); // WHERE

            List<Tuple> results = em.createQuery(criteria)
                    .getResultList();
            if(results == null || results.size() <= 0)
                return "";
            
            return results.get(0).get(0).toString() + results.get(0).get(1).toString();
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
        
    }
    
    public String URIToProgram(String URI){
        return null;
    }
}
