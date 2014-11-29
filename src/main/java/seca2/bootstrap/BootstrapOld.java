/**
 * This is the most important class in the entire application!!! It is a 
 * core that dispatches, load and manage key components of the application.
 * <ul>
 * <li>User Management</li>
 * <li>Program Management</li>
 * <li>Presentation Management</li>
 * </ul>
 * It is important that these parts operate independently of each other and they
 * can be changed/enhanced without having to change the others.
 * 
 */

package seca2.bootstrap;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.module.Program.ProgramModule;
import seca2.bootstrap.module.User.LoginMode;
import seca2.bootstrap.module.User.UserModule;

/**
 *
 * @author vincent.a.lee
 */
/*@URLMappings(mappings={
    @URLMapping(id="home", pattern="/",viewId="/program/index.xhtml"),
    @URLMapping(id="program", pattern="/program/#{program}/",viewId="/program/index.xhtml"),
    @URLMapping(id="install", pattern="/install/",viewId="/program/programs/install/install.xhtml")
})
@URLBeanName("bootstrap")
@Named("bootstrap")
@RequestScoped*/
public class BootstrapOld implements Serializable {
    
    /**
     * Use this for anything. Anything!
     */
    private Map<String,Object> elements;
    
    @Inject @Any
    private Instance<BootstrapModule> modules;
    /**
     * Injected view parameters
     */
    private String program; //which program are you loading
    
    @PostConstruct
    public void init(){
        boolean isPostBack = FacesContext.getCurrentInstance().isPostback();
        System.out.println("This is "+ (isPostBack ? "" : "not a") + " postback");
        for(BootstrapModule module:modules){
            System.out.println("This is module "+module.getClass().getSimpleName());
        }
        //Initialize all parameters
        elements = new HashMap<String,Object>();
        
        //User Management
        String loginBlock = "";
        String loginPage = "";
        switch(userModule.getLoginMode()){
            case BLOCK : loginBlock = LoginMode.BLOCK.include;
                         break;
            case PAGE  : loginPage = LoginMode.PAGE.include;
                         break;
            default    : break;
        }
        elements.put("user-module-login-block", loginBlock);
        elements.put("user-module-login-page", loginPage);
        
        //Program Management
        
        //Template Management
        
        //Error Management
        elements.put("error-module-block", "/programs/error/errorBox.xhtml");
    }
    
    public void checkInstalled(){
        
    }
    
    /**
     * User Management!
     * <p>
     * This is the part where you manage authentication and sessions.
     * <p>
     * A bootstrap class shouldn't have a dependency on a form class. Bootstrap
     * exists even before any forms are called, but what the hack...before I 
     * figure out how to split this entire bootstrap class into Servlet Filters,
     * let's just put it here first...
     * 
     * - Throws IOException because error management module is not up yet, once 
     * up it should allow error module to handle any sort of exceptions.
     * 
     */
    @Inject private UserModule userModule;
    
    //@URLActions(actions={
    //    @URLAction(mappingId="home", onPostback=true),
    //    @URLAction(mappingId="program", onPostback=true)
    //})
    public void checkLogin() throws IOException{
        boolean loggedIn = userModule.checkSessionActive(null);
        
        //Put it inside elements for login mode BLOCK
        this.elements.put("user", loggedIn);
        
        String login_page = elements.get("user-module-login-page").toString();
        if(login_page != null && login_page.length() > 0){
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath()+login_page); //must always include context path when redirecting!!!
        }
            
                    
    }
    
    /**
     * Program Management! 
     * 
     * [2014.10.26] This will be renamed to Navigation Management
     * - Authorization access will be based on MenuItems, not programs.
     * - MenuItems are in turn assigned to programs.
     * 
     * <p>
     * This is the part where you manage what to show depending on what the user 
     * has inputted in the request URL.
     * <p>
     * To be deprecated
     */
    //@Inject 
    private ProgramModule programModule;
    //@URLActions(actions={
    //    @URLAction(mappingId="home", onPostback=false),
    //    @URLAction(mappingId="program", onPostback=false)
    //})
    public void loadProgram(){
        //Let's just get the template up first
        //It's damn ugly coding but no choice....
        System.out.println("Loading program from elements: "+this.elements.get("program"));
        if(this.program == null || this.program.isEmpty()){
            programModule.setCurrentProgramIndex(ProgramModule.DEFAULT_PROGRAM);
            program = programModule.getProgramNames().get(programModule.getCurrentProgramIndex());
        }else{
            programModule.setCurrentProgramIndex(-1);
            for(int i=0; i<programModule.getPrograms().size(); i++){
                String prog = programModule.getPrograms().get(i).getPROGRAM_NAME();
                if(this.program.equalsIgnoreCase(prog)){
                    programModule.setCurrentProgramIndex(i);
                }
            }
            if(programModule.getCurrentProgramIndex() < 0)
                throw new RuntimeException("Cannot find program "+this.program);
        }
        this.elements.put("program", programModule.getCurrentProgram());
        this.elements.put("program-location", "/programs/"+program+"/layout.xhtml"); //I have to use this first...
        
        program = ""; //clear after setting, because a sessionscoped variable will only be injected once.
    }
    
    /**
     * Navigation management
     * <p>
     * This is where you manage your menu items and which page they link to
     * 
     
    @Inject private NavigationModule navigationModule;
    @URLActions(actions={
        @URLAction(mappingId="home", onPostback=false),
        @URLAction(mappingId="program", onPostback=false)
    })
    public void loadNavigation(){
        
    }*/
    
    /**
     * Presentation Management!
     * <p>
     * This is the part where you manage how to present each page nicely.
     */
    //@URLActions(actions={
    //    @URLAction(mappingId="home", onPostback=false),
    //    @URLAction(mappingId="program", onPostback=false)
    //})
    public void loadPresentation(){
        //shortcut for the time being
        this.elements.put("template-location", "/templates/mytemplate/template-layout.xhtml");
    }
    
    /**
     * 
     * @return 
     */
    
    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Map<String, Object> getElements() {
        return elements;
    }

    public void setElements(Map<String, Object> elements) {
        this.elements = elements;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
    // </editor-fold>
}
