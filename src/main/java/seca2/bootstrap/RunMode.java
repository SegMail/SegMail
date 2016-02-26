/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

/**
 *
 * @author LeeKiatHaw
 */
public enum RunMode {
    INSTALL("INSTALL"),
    NORMAL("NORMAL"),
    WEB("WEB");
    
    final String name;
    
    private RunMode(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
    
    public static RunMode getRunMode(String runMode){
        if(runMode.toUpperCase().equals(RunMode.INSTALL))
            return RunMode.INSTALL;
        
        if(runMode.toUpperCase().equals(RunMode.NORMAL))
            return RunMode.NORMAL;
        
        if(runMode.toUpperCase().equals(RunMode.WEB))
            return RunMode.WEB;
        
        return RunMode.NORMAL; //Default
    }
    
}
