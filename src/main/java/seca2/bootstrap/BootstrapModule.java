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
 * 
 * @author LeeKiatHaw
 */
public abstract class BootstrapModule implements Filter {
   
    /*@Inject*/ protected DefaultSites defaultSites = new DefaultSites();
    
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
        
        boolean toContinue = (this.inService() ? this.execute(inputContext, outputContext) : true );
        
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
     */
    protected abstract boolean execute(
            ServletRequest request, 
            ServletResponse response);
    
    /**
     * What will happen if the execute methods fail.
     * 
     * @param request
     * @param response 
     */
    protected abstract void ifFail(
            ServletRequest request, 
            ServletResponse response);
    
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
     * List of all other BootstrapModules that this one is dependent on. If these 
     * dependencies are not present when loading this module, then the bootstrapping 
     * process should fail.
     * 
     * Bad idea for now, as bootstrapping process happens for every request,
     * you don't want to incur a O(n^2) time cost on every request. Unless in the 
     * future we can make bootstrapping process happens in the session scope.
     * 
     * @param <BM>
     * @return 
     */
     // protected abstract <BM extends BootstrapModule> List<BM> bootstrappingDependencies();
    /**
     * 
     * @return 
     */
    protected abstract int executionSequence();
    
    /**
     * Returns the state of this bootstrap module. True means it is in service.
     * False means bypass.
     * 
     * @return 
     */
    protected abstract boolean inService();

    /**
     * This is for initializing the module as a filter when Bootstrap runs.
     * Each module can determine at runtime what is the URL pattern they are
     * required to process.
     * 
     * @param request
     * @param response
     * @return 
     */
    protected abstract String urlPattern(
            );
    
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

            if(this.execute(req, res)){
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
    
}
