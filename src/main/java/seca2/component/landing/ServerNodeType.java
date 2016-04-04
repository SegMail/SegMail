/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.landing;

/**
 *
 * @author LeeKiatHaw
 */
public enum ServerNodeType {
    ERP("ERP"),
    WEB("WEB");
    
    String value;
    
    private ServerNodeType(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    
    
}
