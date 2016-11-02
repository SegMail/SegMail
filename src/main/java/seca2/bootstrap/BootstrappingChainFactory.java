/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * This is a chain of responsibility for all bootstrap modules.
 * 
 * @author LeeKiatHaw
 */
public class BootstrappingChainFactory implements Serializable {
    
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
    private List<BootstrapModule> coreBootstrapModuleList;
    private List<BootstrapModule> nonCoreBootstrapModuleList;
    private BootstrapModule coreHead;
    private BootstrapModule nonCoreHead;
    
    @PostConstruct
    public void init(){
        //All core modules
        coreBootstrapModuleList = new ArrayList<BootstrapModule>();
        coreBootstrapModuleList.addAll(this.generateBootstrapList(this.cModules));
        
        //All noncore modules
        nonCoreBootstrapModuleList = new ArrayList<BootstrapModule>();
        nonCoreBootstrapModuleList.addAll(this.generateBootstrapList(this.Modules));
        
    }

    public BootstrapModule getCoreHead() {
        return coreHead;
    }

    public BootstrapModule getNonCoreHead() {
        return nonCoreHead;
    }

    public List<BootstrapModule> getCoreBootstrapModuleList() {
        return coreBootstrapModuleList;
    }

    public List<BootstrapModule> getNonCoreBootstrapModuleList() {
        return nonCoreBootstrapModuleList;
    }
    
    /**
     * 
     * @return both core and non-core BMs.
     */
    public List<BootstrapModule> getAllBootstrapModuleList() {
        List<BootstrapModule> all = getCoreBootstrapModuleList();
        all.addAll(getNonCoreBootstrapModuleList());
        
        return all;
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
            if(bm.inService()) //Only add it if it's in service.
                moduleList.add(bm);
        }
        
        Collections.sort(moduleList, new BootstrapModuleComparator());
        
        return moduleList;
    }
    
    public BootstrapModule getModuleByName(String name){
        for(BootstrapModule mod : cModules){
            if(name.equals(mod.getName()))
                return mod;
        }
        for(BootstrapModule mod : Modules){
            if(name.equals(mod.getName()))
                return mod;
        }
        return null;
    }
    
    public BootstrapModuleComparator getComparator(){
        return new BootstrapModuleComparator();
    }
}

//Since this class is only used by the chain factory alone, put it here.
class BootstrapModuleComparator implements Comparator<BootstrapModule> {

    @Override
    public int compare(BootstrapModule o1, BootstrapModule o2) {
        //CoreModules will all be ahead of NonCoreModules no matter what
        boolean c1 = o1.getClass().isAnnotationPresent(CoreModule.class);
        boolean c2 = o2.getClass().isAnnotationPresent(CoreModule.class);
        
        int w1 = o1.executionSequence();
        int w2 = o2.executionSequence();
        
        int result = !(c1 ^ c2) ? w1 - w2 : ((c1) ? -1 : 1);
        return result;
        
    }
    
  
}