/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.program;

import eds.component.GenericObjectService;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.user.UserService;
import eds.entity.program.Program;
import eds.entity.program.ProgramAssignment;
import eds.entity.program.Program_;
import eds.entity.user.UserType;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ProgramService implements Serializable {
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;
    
    @EJB private UserService userService;
    @EJB private GenericObjectService genericEntepriseObjectService;
    /**
     * 
     * Only returns 1 result.
     * 
     * @param programName
     * @return
     */
    
    public Program getSingleProgramByName(String programName) {
        List<Program> results = this.genericEntepriseObjectService.getEnterpriseObjectsByName(programName, Program.class);
        if(results == null || results.size() <= 0)
            return null;

        return results.get(0);
    }
    
    /**
     * 
     * @return 
     */
    public List<Program> getAllPrograms() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Program> criteria = builder.createQuery(Program.class);
        Root<Program> sourceEntity = criteria.from(Program.class); //FROM UserType

        criteria.select(sourceEntity); // SELECT *

        List<Program> results = em.createQuery(criteria)
                .getResultList();

        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignProgramToUserType(long programId, long userTypeId) 
            throws RelationshipExistsException, EntityNotFoundException{
        //Get user type first
        //UserType userType = userService.getUserTypeById(userTypeId);
        UserType userType = this.genericEntepriseObjectService.getEnterpriseObjectById(userTypeId, UserType.class);
        if(userType == null)
            throw new EntityNotFoundException("Usertype with ID "+userTypeId+" does not exist!");

        //Get the program object
        Program program = this.getProgramById(programId);
        if(program == null)
            throw new EntityNotFoundException("Program with ID "+programId+" does not exist!");

        //Check if the assignment already exist
        //if(this.checkProgramAuthForUserType(userTypeId, programId))
        //    throw new ProgramAssignmentException("Usertype with ID "+userTypeId+" already has access to Program with ID "+programId+".");
        List<ProgramAssignment> allAssignment = this.genericEntepriseObjectService.getRelationshipsForTargetObject(userTypeId, ProgramAssignment.class);

        ProgramAssignment programAccess1 = new ProgramAssignment(program,userType);
        if(allAssignment != null && allAssignment.contains(programAccess1))
            throw new RelationshipExistsException(programAccess1);

        //For all first assignment, set it as the default one
        if(allAssignment == null || allAssignment.isEmpty())
            programAccess1.setDEFAULT_ASSIGNMENT(true);

        em.persist(programAccess1);
    }
    /**
     * 
     * @param programName
     * @param viewRoot
     * @param displayName
     * @param displayDesc
     * @param isPublic
     * @throws eds.component.data.IncompleteDataException
     * @throws eds.component.data.EntityExistsException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerProgram(String programName, String viewRoot, String displayName, String displayDesc, boolean isPublic)
            throws IncompleteDataException, EntityExistsException {
        //Check if program name is empty
        if(programName == null || programName.length() <= 0)
            throw new IncompleteDataException("Program name cannot be empty!");

        //Check if viewRoot is empty
        //We should not check the format of viewRoot here, as it is View-specific (highly dependent on the view platform eg. JSF, Vaadin, etc)
        if(viewRoot == null || viewRoot.length() <= 0)
            throw new IncompleteDataException("View root cannot be empty!");

        //Check if program name already exist
        Program existingProgram = this.getSingleProgramByName(programName);
        if(existingProgram != null)
            throw new EntityExistsException(existingProgram);

        Program program = new Program();

        //Set basic variables
        program.setPROGRAM_NAME(programName);
        program.setVIEW_ROOT(viewRoot);
        program.setDISPLAY_TITLE(displayName);
        program.setDISPLAY_DESCRIPTION(displayDesc);
        program.setIS_PUBLIC(isPublic);

        em.persist(program);
    }
    
    
    public Program getProgramById(long programId) {
        return em.find(Program.class, programId);
    }
    
    //Translation services
    
    public String getViewRootFromProgramName(String programName) {
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
            
    }
    
    
    public boolean checkProgramAuthForUserType(long usertypeid, String programName) {
        Program program = this.getSingleProgramByName(programName);
        //Always use the first result
        if(program == null)
            return false;
        
        //Program program = programs.get(0);
        return this.checkProgramAuthForUserType(usertypeid, program.getOBJECTID());
    }
    
    
    
    public boolean checkProgramAuthForUserType(long usertypeid, long programid) {
        List<ProgramAssignment> results = this.genericEntepriseObjectService.getRelationshipsForObject(programid,usertypeid,ProgramAssignment.class);

        if(results.size() > 0)
            return true;

        return false;
    }
    
    
    public List<ProgramAssignment> getProgramAssignmentsForUserType(long usertypeid) {
        List<ProgramAssignment> result = this.genericEntepriseObjectService.getRelationshipsForTargetObject(usertypeid, ProgramAssignment.class);

        return result;
    }
    
    /**
     * Optimized method written for ProgramModule. Retrieves the program by name
     * and checks if the given userTypeId is authorized. If it is, return the 
     * program, else returns the default program assigned to the user.
     * 
     * Else do not return the default program for the user.
     * 
     * @param programName
     * @param userTypeId
     * @return 
     */
    
    public Program getProgramForUserType(String programName,long userTypeId){
        List<ProgramAssignment> results = this.genericEntepriseObjectService.getRelationshipsForTargetObject(userTypeId, ProgramAssignment.class);

        //Loop through, return the one that has the program name, else return the default
        Program result = null;
        for(ProgramAssignment p : results){
            if(p.getSOURCE().getPROGRAM_NAME().equalsIgnoreCase(programName))
                return p.getSOURCE();
            if(p.isDEFAULT_ASSIGNMENT()) //This will return the default program even if the target program is not found, not very good design
                result = p.getSOURCE();
        }
        return result;
            
    }
    
}
