/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A bootstrap module mimics a Servlet filter or a Struts interceptor - the 
 * chain of responsibility design pattern. Each BoostrapModule is "strapped" on
 * with the next Bootstrap module which can be called depending on certain 
 * conditions. For example, the UserModule may check if the user is logged in
 * before deciding if it should direct the user to a log in page or the next 
 * BootstrapModule. If the user is logged in, check the role and pass it through 
 * a message object for the next BootstrapModule to process.
 * <br>
 * There are 3 states that a BootstrapModule can be in:
 * <ol>
 * <li>Inactive - not even loaded as a filter during runtime.</li>
 * <li>Active - loaded as a filter.</li>
 * <li>Active but bypass - loaded as a filter but skips the 
 * execute(ServletRequest,ServletResponse) method.</li>
 * </ol>
 * Active state is controlled by the result of the inService() method. Active but 
 * bypass state is controlled by the AND result of all bypass*() methods. In
 * Active but bypass state, the bootstrapping/filter chain processing is continued.
 * 
 * @author LeeKiatHaw
 */
public abstract class BootstrapModule implements Filter {
   
    @Inject protected DefaultKeys defaults;
    
    public static String FACES_CONTEXT = "context";
    
    /**
     * Execute the control logic of this module.
     * 
     * @param request
     * @param response
     * @return true to continue the chain, false to stop the chain.
     * @throws java.lang.Exception
     */
    protected abstract boolean execute(
            ServletRequest request, 
            ServletResponse response) throws Exception;
    
    /**
     * What will happen if the execute methods fail.
     * 
     * @param request
     * @param response 
     * @throws java.lang.Exception 
     */
    protected abstract void ifFail(
            ServletRequest request, 
            ServletResponse response) throws Exception;
    
    /**
     * What happens if an exception is encountered during request processing.
     * <br>
     * This was designed mainly for browser requests. It doesn't work well with 
     * webservice calls.
     * 
     * @param request
     * @param response 
     */
    protected abstract void ifException(
            ServletRequest request, ServletResponse response, Exception ex);
    
    /**
     * What happens if the request is for a file resource.
     * 
     * Modules don't really need to do anything, they can just return true to
     * bypass file processing. Maybe there will be a FileModule that will use it.
     * 
     * Let's not use this first
     * 
     * @param request
     * @param response 
     */
    //protected abstract boolean ifFileRequest(
    //        ServletRequest request, 
    //        ServletResponse response);
    
    /**
     * List of all other BootstrapModules that this one is dependent on. If these 
     * dependencies are not present when loading this module, then the bootstrapping 
     * process should fail.
     * 
     * Bad idea for now, as bootstrapping process happens for every request,
     * you don't want to incur a O(n^2) time cost on every request. Unless in the 
     * future we can make bootstrapping process happens in the session scope.
     * 
     * We have made bootstrapping happen during deployment, but this is still a 
     * bad idea because it is completely useless for core modules since there 
     * won't be many of them. It could be used for noncore modules in the future,
     * but not worth the time now.
     * 
     * @param <BM>
     * @return 
     */
     // protected abstract <BM extends BootstrapModule> List<BM> bootstrappingDependencies();
    /**
     * 
     * @return int
     */
    protected abstract int executionSequence();
    
    /**
     * To determine if this BootstrapModule should be loaded during deployment.
     * True = load, false = don't load.
     * 
     * @return boolean
     */
    protected abstract boolean inService();
    
    /**
     * Indicate if this BootstrapModule should run the execute(ServletRequest,
     * ServletResponse) method during an Installation. If true, the bootstrapping/
     * filter chain processing will continue without executing the method. If 
     * false, it will continue to execute() and let the results determine if it
     * should continue to the next module/filter or fail.
     * 
     * @return boolean
     */
    protected abstract boolean bypassDuringInstall();
    
    /**
     * Indicate if this BootstrapModule should run the execute(ServletRequest,
     * ServletResponse) method during normal operations. If true, the bootstrapping/
     * filter chain processing will continue without executing the method. If 
     * false, it will continue to execute() and let the results determine if it
     * should continue to the next module/filter or fail.
     * 
     * @return boolean
     */
    protected abstract boolean bypassDuringNormal();
    
    /**
     * Indicate if this BootstrapModule should run the execute(ServletRequest,
     * ServletResponse) method during web operations. If true, the bootstrapping/
     * filter chain processing will continue without executing the method. If 
     * false, it will continue to execute() and let the results determine if it
     * should continue to the next module/filter or fail.
     * 
     * @return boolean
     */
    protected abstract boolean bypassDuringWeb();

    /**
     * This is for initializing the module as a filter when Bootstrap runs.
     * Each module can determine at runtime what is the URL pattern they are
     * required to process.
     * 
     * @return 
     */
    protected abstract String urlPattern();
    
    /**
     * Tells Bootstrap which dispatch type needs to execute this filter - eg. 
     * REQUEST, FORWARD
     * @return 
     */
    protected abstract List<DispatcherType> getDispatchTypes();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            String runModeString = req.getServletContext().getInitParameter(defaults.RUN_MODE);
            RunMode runMode = RunMode.getRunMode(runModeString);
            
            if(
                    (runMode.equals(RunMode.INSTALL) && this.bypassDuringInstall()) || 
                    (runMode.equals(RunMode.ERP) && this.bypassDuringNormal()) ||
                    (runMode.equals(RunMode.WEB) && this.bypassDuringWeb()) ||
                    this.execute(req, res)){
                chain.doFilter(request, response);
                return; // Very important!
            }

            this.ifFail(request, response);
            
        } catch (Exception ex) {
            this.ifException(request, response, null);
            ex.printStackTrace(System.out);
        }
    }

    @Override
    public void destroy() {
        
    }
    
    public abstract String getName();
    
    protected void reportException(Exception ex, UserRequestContainer requestContainer){
        requestContainer.setError(true);
        requestContainer.setErrorMessage(this.getName()+": "+ex.getMessage());
        requestContainer.setErrorStackTrace(ex.getStackTrace());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException{
        
    }
    
    
}
