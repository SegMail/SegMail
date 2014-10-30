/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.Map;

/**
 * A bootstrap module mimics a Servlet filter or a Struts interceptor - 
 * 
 * @author LeeKiatHaw
 */
public abstract class BootstrapModule {
    
    /**
     * The method that is used for 
     * @param input
     * @param output 
     */
    protected abstract void doStuff(Map<String,Object> input,Map<String,Object> output);
    
    
    
}
