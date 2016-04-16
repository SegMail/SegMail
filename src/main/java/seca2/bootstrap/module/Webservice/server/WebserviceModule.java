/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.server;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.UserRequestContainer;

/**
 *
 * @author LeeKiatHaw
 */
@CoreModule
public class WebserviceModule extends BootstrapModule {

    @Inject
    UserRequestContainer requestContainer;

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) throws Exception {
        
        boolean isWS = checkWScalls(request, response);
        requestContainer.setWebservice(isWS);
        
        return true;

    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) throws Exception {

    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {

    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 190;
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
        return true;
    }

    @Override
    protected String urlPattern() {
        return "/*";
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<>();
        dispatchTypes.add(DispatcherType.REQUEST);

        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "WebserviceModule";
    }
    
    private boolean checkWScalls(ServletRequest request, ServletResponse response) throws SOAPException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        
        boolean queryStringIsWSDL = checkQueryString(req);
        boolean contentTypeIsSOAPXML = checkContentType(req);
        
        return queryStringIsWSDL || contentTypeIsSOAPXML;
    }
    
    private boolean checkQueryString(HttpServletRequest req) {
        String queryString = req.getQueryString();
        
        return (queryString != null && 
                    (
                        queryString.startsWith("wsdl") ||
                        queryString.startsWith("tester") ||
                        queryString.startsWith("xsd")
                    )
                );
    }
    
    private boolean checkContentType(HttpServletRequest req) {
        String contentType = req.getContentType();
        
        return
                contentType != null &&
                (contentType.toUpperCase().contains("TEXT/XML") || 
                contentType.toUpperCase().contains("SOAP"));
    }

}
