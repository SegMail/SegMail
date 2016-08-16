/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.REST;

import eds.component.user.UserService;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import eds.entity.user.UserType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 * Only works for REST calls, not SOAP calls because all REST calls should be 
 * directed to /rest/*
 * 
 * @author LeeKiatHaw
 */
@CoreModule
public class RestAuthenticationModule extends BootstrapModule implements Serializable {
    
    @Inject UserRequestContainer userRequestContainer;
    @Inject UserSessionContainer userSessionContainer;
    
    @EJB UserService userService;

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) throws Exception {
        
        //The first parameter should be the API key
        //If it is not, stop the processing
        //Get the API key as part of the URL first
        String key = "";
        List<String> orderedParams = userRequestContainer.getPathParser().getOrderedParams();
        if(orderedParams != null && !orderedParams.isEmpty())
            key = orderedParams.get(0);
            
        Map<String,String[]> paramMap = request.getParameterMap();
        
        if(paramMap != null && !paramMap.isEmpty() && paramMap.get("a")[0] != null && !paramMap.get("a")[0].isEmpty())
            key = paramMap.get("a")[0];
        
        //Retrieve the UserAccount, User objects and initialize them into UserSessionContainer
        UserAccount userAccount = userService.getUserAccountByAPIKey(key);
        if(userAccount == null)
            return false;
        
        User user = userAccount.getOWNER();
        if(user == null)
            return false;
        userSessionContainer.setUser(user);
        
        UserType userType = user.getUSERTYPE();
        if(userType == null || !userType.isWS_ACCESS() )
            return false;
        userSessionContainer.setUserType(userType);
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 700;
    }

    @Override
    protected boolean inService() {
        return true;
    }

    @Override
    protected boolean bypassDuringInstall() {
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

    @Override
    protected String urlPattern() {
        String restPath = System.getProperty(defaults.WEBSERVICE_PATH);
        if(!restPath.startsWith("/"))
            restPath = "/"+restPath;
        if(!restPath.endsWith("/"))
            restPath = restPath+"/";
        
        restPath = restPath + "*";
        
        return restPath;
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<>();
        dispatchTypes.add(DispatcherType.REQUEST);

        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "RestAuthenticationModule";
    }
    
}
