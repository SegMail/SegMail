/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import eds.utilities.EntityExplorer;

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
     * 
     * Injected Instances generates a Weld proxy which properties cannot be set
     * normally. 
     */
    @Inject @NonCoreModule private Instance<BootstrapModule> Modules;
    @Inject @CoreModule private Instance<BootstrapModule> cModules;
    /**
     * 
     */
    private List<BootstrapModule> bootstrapModuleList;
    private BootstrapModule head;
    
    @PostConstruct
    public void init(){
        bootstrapModuleList = new ArrayList<BootstrapModule>();
        bootstrapModuleList.addAll(this.generateBootstrapList(this.cModules));
        bootstrapModuleList.addAll(this.generateBootstrapList(this.Modules));
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
        
        for(BootstrapModule bm : modules){
            moduleList.add(bm);
        }
        
        return moduleList;
    }
    
    public BootstrapModule constructBoostrapChain(){
        return this.constructBoostrapChain(this.bootstrapModuleList);
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
     * Not recommended, as components are not injected automatically.
     * 
     * @return
     */
    public List<BootstrapModule> generateBootstrapList() {
        try{
            eds.utilities.Package root = new eds.utilities.Package();
            root.push("seca2").push("bootstrap");
            List<BootstrapModule> result = new ArrayList<>();
            List<Class> allBootstrapClasses = 
                EntityExplorer.collectClasses(root, EntityExplorer.getClassLoader());

            for(Class c : allBootstrapClasses){
                if(BootstrapModule.class.isAssignableFrom(c)){
                    BootstrapModule newInstance = (BootstrapModule) c.getConstructor().newInstance();
                    result.add(newInstance);
                }

            }
            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}

//Since this class is only used by the chain factory alone, put it here.
class BootstrapModuleComparator implements Comparator<BootstrapModule> {

    @Override
    public int compare(BootstrapModule o1, BootstrapModule o2) {
        int w1 = o1.executionSequence();
        int w2 = o2.executionSequence();
        
        return w2 - w1;
    }
    
}