/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

/**
 *
 * @author LeeKiatHaw
 */
public enum ServerJNDIResourceType {
    EJB("EJB"),
    JMS_CONNECTION("JMS_CONNECTION");
    
    public final String label;
    
    private ServerJNDIResourceType(String label){
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
    
    
}
