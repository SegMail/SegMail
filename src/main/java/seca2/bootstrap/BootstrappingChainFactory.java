/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import seca2.utilities.EntityExplorer;

/**
 * This is a chain of responsibility for all bootstrap modules.
 * 
 * @author LeeKiatHaw
 */
public class BootstrappingChainFactory {
    
    /**
     * Currently we are experimenting with executing all modules for all requests.
     * We might change the mode of execution in the future, eg. execute Session
     * and Request modules differently.
     */
    @Inject @Any private Instance<BootstrapModule> Modules;
    
    /**
     * 
     */
    private List<BootstrapModule> bootstrapModuleList;
    private BootstrapModule head;
    
    @PostConstruct
    public void init(){
        //Depending on the environment (SE or EE), call different methods
        if(Modules == null){
            bootstrapModuleList = this.generateBootstrapList();
        } else {
            bootstrapModuleList = this.generateBootstrapList(Modules);
        }
        head = this.constructBoostrapChain(this.bootstrapModuleList);
    }

    public BootstrapModule getHead() {
        return head;
    }

    public List<BootstrapModule> getBootstrapModuleList() {
        return bootstrapModuleList;
    }
    
    
    
    //Helper methods
    
    /**
     * Generates a List of BootstrapModules from the injected Instance.
     * 
     * @param modules
     * @return 
     */
    public List<BootstrapModule> generateBootstrapList(Instance<BootstrapModule> modules){
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
    public BootstrapModule constructBoostrapChain(List<BootstrapModule> moduleList){
        /**
         * Impt! moduleList must be sorted here, if not the next step will return
         * a chain in the wrong order.
         */
        Collections.sort(moduleList, new BootstrapModuleComparator());
        
        /**
         * Traverse through the sorted moduleList and build up the chain. Start 
         * by getting the first BootstrapModule, strap on a null object, go to 
         * the next BootstrapModule and strap on to the previous one. 
         */
        Iterator<BootstrapModule> i = moduleList.iterator();
        BootstrapModule tempHead = null;
        while(i.hasNext()){
            BootstrapModule nextHead = i.next();
            nextHead.strapNext(tempHead);
            tempHead = nextHead;
            
        }
        
        return tempHead;
    }
    
    /**
     * A non-Java EE method to get all instances of BoostrapModule class.
     * 
     * @return
     * @throws ClassNotFoundException
     * @throws Exception 
     */
    public List<BootstrapModule> generateBootstrapList() {
        try{
            seca2.utilities.Package root = new seca2.utilities.Package();
            root.push("seca2").push("bootstrap");
            List<BootstrapModule> result = new ArrayList<>();
            List<Class> allBootstrapClasses = 
                EntityExplorer.collectClasses(root, EntityExplorer.getClassLoader());

            for(Class c : allBootstrapClasses){
                if(c.isAssignableFrom(BootstrapModule.class)){
                    BootstrapModule newInstance = (BootstrapModule) c.getConstructor((Class) null).newInstance((Object) null);
                    result.add(newInstance);
                }

            }
            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}
