/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * This is a chain of responsibility for all bootstrap modules.
 * 
 * @author LeeKiatHaw
 */
public class BootstrappingChain {
    
    /**
     * Currently we are experimenting with executing all modules for all requests.
     * We might change the mode of execution in the future, eg. execute Session
     * and Request modules differently.
     */
    @Inject @Any private Instance<BootstrapModule> Modules;
    
    /**
     * 
     */
    private List<BootstrapModule> bootstrapOrderedList;
    
    @PostConstruct
    public void init(){
        this.bootstrapOrderedList = this.generateBootstrapList(Modules);
    }
    
    public void executeChain(Map<String,Object> inputContext, Map<String,Object> outputContext){
        // 1.How to stop the execution?
        // 2.How to allow modules in the chain to redirect/forward HTTP requests?
        
        for(BootstrapModule module : this.bootstrapOrderedList){
            module.execute(inputContext, outputContext);
        }
    }
    
    //Helper methods
    
    /**
     * Generates a List of BootstrapModules from the injected Instance.
     * 
     * @param modules
     * @return 
     */
    private List<BootstrapModule> generateBootstrapList(Instance<BootstrapModule> modules){
        List<BootstrapModule> moduleList = new ArrayList<BootstrapModule>();
        Iterator<BootstrapModule> i = modules.iterator();
        
        while(i.hasNext()){
            moduleList.add(i.next());
        }
        
        return moduleList;
    }
    
    /**
     * Returns the head of a BootstrapChain
     * @param moduleList
     * @return 
     */
    private BootstrapModule constructBoostrapChain(Instance<BootstrapModule> modules){
        /**
         * Impt! moduleList must be sorted here, if not the next step will return
         * a chain in the wrong order.
         */
        List<BootstrapModule> moduleList = this.generateBootstrapList(modules);
        Collections.sort(moduleList, new BootstrapModuleComparator());
        
        /**
         * Traverse through the sorted moduleList and build up the chain. Start 
         * by getting the first BootstrapModule, strap on a null object, go to 
         * the next BootstrapModule and strap on to the previous one. 
         */
        Iterator<BootstrapModule> i = moduleList.iterator();
        BootstrapModule head = null;
        while(i.hasNext()){
            BootstrapModule nextHead = i.next();
            nextHead.strapNext(head);
            head = nextHead;
            
        }
        
        return head;
    }
}
