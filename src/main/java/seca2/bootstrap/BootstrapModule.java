/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.Map;

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
    
    private BootstrapModule next;
    
    /**
     * As with http://www.javaworld.com/article/2072857/java-web-development/the-chain-of-responsibility-pattern-s-pitfalls-and-improvements.html,
     * this is a non-classic CoR implementation where the base class decides to 
     * trigger the next responsibility in the chain.
     * 
     * @param inputContext
     * @param outputContext 
     */
    public void start(Map<String,Object> inputContext, Map<String,Object> outputContext){
        this.execute(inputContext, outputContext);
        
        if(next != null)
            next.start(inputContext, outputContext);
    }
    
    public void strapNext(BootstrapModule next){
        this.next = next;
    }
    
    /**
     * The method that is used for 
     * @param inputContext
     * @param outputContext
     * @param chain
     * @param input
     * @param output 
     */
    protected abstract void execute(
            Map<String,Object> inputContext,
            Map<String,Object> outputContext);
    
    protected abstract int executionSequence();
    
}
