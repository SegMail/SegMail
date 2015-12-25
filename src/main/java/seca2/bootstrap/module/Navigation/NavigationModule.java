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

    @EJB private NavigationService navigationService;
    
    @Inject private UserSessionContainer sessionContainer;
    @Inject private UserRequestContainer requestContainer;
    
    private final String menuRoot = "/programs/menu/top_menu.xhtml";
    
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
        
        UserType userType = sessionContainer.getUserType();
        
        //For all other programs which changes menu items assignment, just nullify menu attribute of the sessionContainer
        
        String currentProgram = requestContainer.getProgramName();
        if(sessionContainer.getMenu() == null ){
            List<MenuItem> menuItems = navigationService.getAllMenuItemsForUsertype(userType.getOBJECTID());
            List<MenuItemContainer> menuItemContainers = new ArrayList<MenuItemContainer>();
            for(MenuItem menuItem : menuItems){
                MenuItemContainer container = new MenuItemContainer();
                container.setMenuItem(menuItem);
                container.setContextPath(contextPath);
                menuItemContainers.add(container);
            }
            sessionContainer.setMenu(menuItemContainers);
        }
        //Set active if the path info is the URL of the menuitem
        for(MenuItemContainer menuItemCont : sessionContainer.getMenu()){
            menuItemCont.setActive(menuItemCont.containsProgram(currentProgram));
        }
        
        requestContainer.setMenuLocation(menuRoot);
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE+4;
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

}
