/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Navigation;

import eds.component.navigation.NavigationService;
import eds.entity.navigation.MenuItem;
import eds.entity.user.UserType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import seca2.bootstrap.module.Path.LogicalPathParser;


/**
 * Builds the navigation structure for the user.
 *
 * @author KH
 */
@CoreModule
public class NavigationModule extends BootstrapModule implements Serializable {

    @EJB private NavigationService navigationService;
    
    @Inject private UserSessionContainer sessionContainer;
    @Inject private UserRequestContainer requestContainer;
    
    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        //IMO, a bug in the 3 above methods
        contextPath = (contextPath == null) ? "" : contextPath;
        servletPath = (servletPath == null) ? "" : servletPath;
        pathInfo = (pathInfo == null) ? "" : pathInfo;
        
        /*if (SegURLResolver.getResolver().addExclude("index.xhtml").containsFile(((HttpServletRequest) request).getRequestURI())) {
            return true;
        }*/
        if (requestContainer.getPathParser().containsFileResource())
            return true;
        
        UserType userType = sessionContainer.getUserType();
        
        //For all other programs which changes menu items assignment, just nullify menu attribute of the sessionContainer
        
        String currentProgram = requestContainer.getProgramName();
        List<MenuItemContainer> existingMenuItems = sessionContainer.getMenu();//debug
        if(sessionContainer.getMenu() == null ){
            List<MenuItem> privateMenuItems = ((userType == null) ? (new ArrayList<MenuItem>()) :
                    navigationService.getAllMenuItemsForUsertype(userType.getOBJECTID()));
            List<MenuItem> publicMenuItems = navigationService.getAllPublicMenuItems();
            
            List<MenuItemContainer> menuItemContainers = new ArrayList<>();
            
            //Add all private menuitems
            for(MenuItem menuItem : privateMenuItems){
                MenuItemContainer container = new MenuItemContainer();
                container.setMenuItem(menuItem);
                container.setContextPath(contextPath);
                if(!menuItemContainers.contains(container)) //Don't add duplicates
                    menuItemContainers.add(container);
            }
            //All all public menuitems
            for(MenuItem menuItem : publicMenuItems){
                MenuItemContainer container = new MenuItemContainer();
                container.setMenuItem(menuItem);
                container.setContextPath(contextPath);
                if(!menuItemContainers.contains(container)) //Public menuitems might be added twice
                    menuItemContainers.add(container);
            }
            
            sessionContainer.setMenu(menuItemContainers);
        }
        //Set active if the path info is the URL of the menuitem
        for(MenuItemContainer menuItemCont : sessionContainer.getMenu()){
            LogicalPathParser newParser = new LogicalPathParser(
                    menuItemCont.getURL(),
                    requestContainer.getPathParser().getViewId(), 
                    requestContainer.getPathParser().getServletPath()
            );
            
            menuItemCont.setActive(newParser.getProgram().equals(requestContainer.getProgramName()));
        }
        
        String menuRoot = request.getServletContext().getInitParameter(defaults.DEFAULT_MENU_LOCATION);
        
        requestContainer.setMenuLocation(menuRoot);
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE+600;
    }

    @Override
    protected boolean inService() {
        return true;
    }

    @Override
    protected String urlPattern() {
        return "/program/*";
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<DispatcherType>();
        dispatchTypes.add(DispatcherType.REQUEST);
        dispatchTypes.add(DispatcherType.FORWARD);

        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "NavigationModule";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected boolean bypassDuringInstall() {
        //return false; 
        /*
         * Because after installation, when you change mode to ERP, upon logging 
         * in it will be noted that you have already instantiate menuitems and 
         * not reload it again as you will still be in the same session. In 
         * general, it is best not to load any EnterpriseObject in BootstrapModules
         * during installation.
         */
        return true;
    }

    @Override
    protected boolean bypassDuringNormal() {
        return false;
    }
    
    @Override
    protected boolean bypassDuringWeb() {
        return false;
    }

}
