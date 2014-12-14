/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.Map;
import javax.inject.Inject;

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
public abstract class BootstrapModule {
   
    @Inject protected DefaultValues defaultValues;
    @Inject protected DefaultSites defaultSites;
    
    public static String FACES_CONTEXT = "context";
    
    private BootstrapModule next;
    
    /**
     * As with http://www.javaworld.com/article/2072857/java-web-development/the-chain-of-responsibility-pattern-s-pitfalls-and-improvements.html
     * this is a non-classic CoR implementation where the base class decides to 
     * trigger the next responsibility in the chain.
     * 
     * @param inputContext
     * @param outputContext 
     */
    public void start(BootstrapInput inputContext, BootstrapOutput outputContext){
        System.out.println("BootstrapModule "+this.getClass().getSimpleName()+" started.");
        
        boolean toContinue = (this.inService() ? this.execute(inputContext, outputContext) : true );
        
        if(next != null && toContinue)
            next.start(inputContext, outputContext);
    }
    
    public void strapNext(BootstrapModule next){
        this.next = next;
    }
    
    /**
     * Execute the control logic of this module.
     * 
     * @param inputContext
     * @param outputContext
     * @return true to continue the chain, false to stop the chain.
     */
    protected abstract boolean execute(
            BootstrapInput inputContext,
            BootstrapOutput outputContext);
    
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
}
