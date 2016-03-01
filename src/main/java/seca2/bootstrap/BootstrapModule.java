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
import javax.servlet.http.HttpSession;

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
    
    private BootstrapModule next;
    /**
     * This is injected during runtime and it should be order-independent, ie. 
     * each module only loads it and use it in their own way, it does not matter
     * which module processed these 2 instances first, and the results should be 
     * the same. Eg. If UserModule executes before program module, and the user 
     * is not loggedin, it should redirect the user to the login page. If the 
     * program module executes first, it would process only request starting with
     * "/program/" and bypass the request to user module, which would then still
     * redirect the user to the login page. 
     */
    //@Inject private BootstrapInput bootstrapInput;
    //@Inject private BootstrapOutput bootstrapOutput;
    
    /**
     * As with http://www.javaworld.com/article/2072857/java-web-development/the-chain-of-responsibility-pattern-s-pitfalls-and-improvements.html
     * this is a non-classic CoR implementation where the base class decides to 
     * trigger the next responsibility in the chain.
     * 
     * @param inputContext
     * @param outputContext 
     */
    /*public void start(BootstrapInput inputContext, BootstrapOutput outputContext){
        System.out.println("BootstrapModule "+this.getClass().getSimpleName()+" started.");
        
        boolean toContinue = (this.active() ? this.execute(inputContext, outputContext) : true );
        
        if(next != null && toContinue)
            next.start(inputContext, outputContext);
        
        if(!toContinue)
            System.out.println("Bootstrap processing stopped at "+this.getClass().getSimpleName()+".");
    }*/
    
    public void strapNext(BootstrapModule next){
        this.next = next;
    }
    
    public void strapNextAtLastPos(BootstrapModule next){
        BootstrapModule thisPos = this;
        while(thisPos.next != null){
            thisPos = thisPos.next;
        }
        thisPos.strapNext(next);
    }
    
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
     */
    protected abstract void ifFail(
            ServletRequest request, 
            ServletResponse response) throws Exception;
    
    /**
     * What happens if an exception is encountered during request processing.
     * 
     * @param request
     * @param response 
     */
    protected abstract void ifException(
            ServletRequest request, 
            ServletResponse response);
    
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
     * @param request
     * @param response
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
            HttpSession session = req.getSession(false);

            //boolean install = Boolean.parseBoolean(req.getServletContext().getInitParameter(defaults.INSTALL));
            String runModeString = req.getServletContext().getInitParameter(defaults.RUN_MODE);
            RunMode runMode = RunMode.getRunMode(runModeString);
            
            if(
                    (runMode.equals(RunMode.INSTALL) && this.bypassDuringInstall()) || 
                    (runMode.equals(RunMode.NORMAL) && this.bypassDuringNormal()) ||
                    (runMode.equals(RunMode.WEB) && this.bypassDuringWeb()) ||
                    this.execute(req, res)){
                chain.doFilter(request, response);
                return; // Very important!
            }

            this.ifFail(request, response);
            
        } catch (Exception ex) {
            this.ifException(request, response);
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
