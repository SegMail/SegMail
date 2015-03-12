/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Navigation;

import eds.component.data.DBConnectionException;
import eds.component.navigation.NavigationService;
import eds.entity.navigation.MenuItem;
import eds.entity.program.Program;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.GlobalValues;
import seca2.bootstrap.module.User.UserSession;

/**
 * Builds the navigation structure for the user.
 *
 * @author KH
 */
//@Named("NavigationModule")
//@SessionScoped
//@BootstrapSession
//@BootstrapType(postback=false)
@CoreModule
public class NavigationModule extends BootstrapModule implements Serializable {

    public static final String CONTEXT_PATH_TOKEN = "#{site_url}";
    @EJB
    private NavigationService navigationService;
    //A MenuContainer should be inside a UserContainer, so there's no need for so many containers.
    //@Inject private MenuContainer menuContainer; //This module initializes the menu so that other programs and components can use it
    @Inject
    private UserSession userContainer;

    @Inject
    private GlobalValues globalValues;

    private List<String> programNames; //stud at this moment
    private List<Program> programs2;
    private int currentProgramIndex;

    private List<MenuItem> menuItems;

    @PostConstruct
    public void init() {
        //Construct menuTree from DB
        //create a stub first, next time then we'll implement the actual thing
        programNames = new ArrayList<String>();

        programNames.add("test");
        programNames.add("sendmail");
        programNames.add("signupforms");
        programNames.add("lists");
        programNames.add("subscribers");
        programNames.add("campaigns");
        programNames.add("mysettings");

        programs2 = new ArrayList<Program>();

        for (int i = 0; i < programNames.size(); i++) {
            Program program = new Program();
            program.setPROGRAM_NAME(programNames.get(i));

            String dir = programNames.get(i).toLowerCase();
            program.setOBJECTID(i);
            program.setBEAN_DIRECTORY("seca2.program." + dir);
            program.setVIEW_DIRECTORY("/program/" + dir + "/");
            program.setVIEW_ROOT("/programs/" + dir + "/layout.xhtml");
            //program.setPROGRAM_ID(i);//not correct, just for the time being

            programs2.add(program);

        }
    }

    public List<MenuItem> getAllMenuList() throws DBConnectionException {
        return navigationService.getAllMenuItems();
    }

    @Override
    protected int executionSequence() {
        return -98;
    }

    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        //BootstrapModules should not access database, but let individual forms do it 
        /*if(menuContainer.getAllMenuItems() == null){
         try {
         menuContainer.setAllMenuItems(this.getAllMenuList());
         } catch (DBConnectionException ex) {
         outputContext.setPageRoot(this.defaultSites.ERROR_PAGE);
         return false;
         }
         }*/
        outputContext.setMenuRoot("/programs/menu/top_menu.xhtml");
        if (userContainer != null && userContainer.isLoggedIn()) {
            //What else should I do here?

        }
        try {
            //Build the menu tree here.
            //For each menu item, if it is a program type, prepend the context path.
            //If it is a URL, leave it as it is
            menuItems = this.navigationService.getAllMenuItems();
            
            //Encapsulate all menuItems into a MenuItemContainer
            List<MenuItemContainer> menuItemContainers = new ArrayList<MenuItemContainer>();
            for(MenuItem menuItem : menuItems){
                MenuItemContainer container = new MenuItemContainer();
                container.setMenuItem(menuItem);
                
                container.setActive(false);
                //Set active if the path info is the URL of the menuitem
                if(menuItem.getMENU_ITEM_URL()
                        .equalsIgnoreCase(
                                globalValues.getPROGRAM_CONTEXT_NAME()+"/"+inputContext.getProgram()+"/")){
                    container.setActive(true);
                }
                menuItemContainers.add(container);
            }
            
            outputContext.getNonCoreValues().put("TEST_MENU2", menuItemContainers);
            //outputContext.getNonCoreValues().put("TEST_MENU", this.programs2);
        } catch (DBConnectionException ex) {
            //set error page
            return false;
        }

        //As of 20141210, I can't find any reason why this module should stop the
        //entire chain processing, so we'll just return true for now.
        return true;
    }

    @Override
    protected boolean inService() {
        return true;
    }

    public List<String> getProgramNames() {
        return programNames;
    }

    public void setProgramNames(List<String> programs) {
        this.programNames = programs;
    }

    public List<Program> getPrograms2() {
        return programs2;
    }

    public void setPrograms2(List<Program> programs) {
        this.programs2 = programs;
    }

    public Program getCurrentProgram() {
        return this.programs2.get(this.currentProgramIndex);
    }

    public int getCurrentProgramIndex() {
        return currentProgramIndex;
    }

    public void setCurrentProgramIndex(int currentProgramIndex) {
        this.currentProgramIndex = currentProgramIndex;
    }

}
