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
    ERP("ERP"),
    WEB("WEB");
    
    final String name;
    
    private RunMode(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
    
}
