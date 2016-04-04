/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.landing;

import java.util.ArrayList;
import java.util.List;

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
    
    public static List<String> getNodeTypesString(){
        ServerNodeType[] allNodeTypes = ServerNodeType.values();
        List<String> list = new ArrayList<>();
        
        for(ServerNodeType nodeType : allNodeTypes)
            list.add(nodeType.value);
        
        return list;
    }
    
    public static ServerNodeType getNodeType(String type){
        ServerNodeType[] allNodeTypes = ServerNodeType.values();
        for(ServerNodeType s : allNodeTypes) {
            if (s.value.equals(type))
                return s;
        }
        return ERP; //Default
    }
}
