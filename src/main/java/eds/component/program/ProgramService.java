/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.program;

import eds.component.GenericEnterpriseObjectService;
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
import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import eds.entity.EnterpriseObject;
import eds.entity.EnterpriseRelationship;
import eds.entity.EnterpriseRelationship_;
import eds.entity.program.Program;
import eds.entity.program.ProgramAssignment;
import eds.entity.program.ProgramAssignment_;
import eds.entity.program.Program_;
import eds.entity.user.User;
import eds.entity.user.UserType;
import javax.persistence.criteria.Join;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ProgramService implements Serializable {
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;
    
    @EJB private UserService userService;
    @EJB private GenericEnterpriseObjectService genericEntepriseObjectService;
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
            //UserType userType = userService.getUserTypeById(userTypeId);
            UserType userType = this.genericEntepriseObjectService.getEnterpriseObjectById(userTypeId, UserType.class);
            if(userType == null)
                throw new ProgramAssignmentException("Usertype with ID "+userTypeId+" does not exist!");

            //Get the program object
            Program program = this.getProgramById(programId);
            if(program == null)
                throw new ProgramAssignmentException("Program with ID "+programId+" does not exist!");

            //Check if the assignment already exist
            if(this.checkProgramAuthForUserType(userTypeId, programId))
                throw new ProgramAssignmentException("Usertype with ID "+userTypeId+" already has access to Program with ID "+programId+".");
            
            //Create bidrectional relationships
            ProgramAssignment programAccess1 = new ProgramAssignment();
            programAccess1.setSOURCE(program);
            programAccess1.setTARGET(userType);

            ProgramAssignment programAccess2 = new ProgramAssignment();
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
     * @throws EDS.component.program.ProgramRegistrationException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerProgram(String programName, String viewDir, String viewRoot, String beanDir)
            throws DBConnectionException, ProgramRegistrationException {
        try{
            //Check if program name is empty
            if(programName == null || programName.length() <= 0)
                throw new ProgramRegistrationException("Program name cannot be empty!");
            
            //Check if viewRoot is empty
            //We should not check the format of viewRoot here, as it is View-specific (highly dependent on the view platform eg. JSF, Vaadin, etc)
            if(viewRoot == null || viewRoot.length() <= 0)
                throw new ProgramRegistrationException("View root cannot be empty!");
            
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
            //program.setOBJECT_NAME(programName);//Do this in ProgramListener
            
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
                    //sourceEntity.get(Program_.VIEW_DIRECTORY),
                    sourceEntity.get(Program_.VIEW_ROOT)); // SELECT VIEW_DIRECTORY, VIEW_ROOT
            
            criteria.where(builder.equal(sourceEntity.get(Program_.PROGRAM_NAME), programName)); // WHERE

            List<Tuple> results = em.createQuery(criteria)
                    .getResultList();
            if(results == null || results.size() <= 0)
                return "";
            
            return results.get(0).get(0).toString();// + results.get(0).get(1).toString();
            
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
    public boolean checkProgramAuthForUserType(long usertypeid, String programName) 
            throws DBConnectionException{
        
        List<Program> programs = this.getProgramByName(programName);
        //Always use the first result
        if(programs == null || programs.size() <=0)
            return false;
        
        Program program = programs.get(0);
        return this.checkProgramAuthForUserType(usertypeid, program.getOBJECTID());
    }
    
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean checkProgramAuthForUserType(long usertypeid, long programid) 
            throws DBConnectionException{
        try{
            //This is so much easier than checkProgramAuthForUserType(long usertypeid, String programName)
            //but there won't be much business case where the client knows the programid. Most of 
            //the time the programName is requested instead, so this would be less frequently used.
            /*CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ProgramAssignment> criteria = builder.createQuery(ProgramAssignment.class);
            Root<ProgramAssignment> sourceEntity = criteria.from(ProgramAssignment.class); //FROM ProgramAssignment
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(ProgramAssignment_.SOURCE), usertypeid));
            criteria.where(builder.equal(sourceEntity.get(ProgramAssignment_.TARGET), programid));
            */
            
            List<EnterpriseRelationship> results = this.genericEntepriseObjectService.getRelationshipsForObjects(usertypeid, programid);
            
            /*List<ProgramAssignment> results = em.createQuery(criteria)
                    .getResultList();*/
            
            if(results.size() > 0)
                return true;
            
            return false;
            
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
    public List<ProgramAssignment> getProgramAssignmentsForUserType(long usertypeid)
            throws DBConnectionException{
        try{
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ProgramAssignment> criteria = builder.createQuery(ProgramAssignment.class);
            Root<ProgramAssignment> sourceEntity = criteria.from(ProgramAssignment.class); //FROM ProgramAssignment
            
            criteria.select(sourceEntity); // SELECT *
            criteria.where(builder.equal(sourceEntity.get(ProgramAssignment_.SOURCE), usertypeid));
            
            List<ProgramAssignment> result = em.createQuery(criteria)
                    .getResultList();
            
            return result;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}
