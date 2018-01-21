/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.navigation;

/**
 *
 * @author LeeKiatHaw
 */
public enum MENU_GROUP {
    
    LEFT("LEFT"),
    PROFILE("PROFILE"),
    TOP("TOP");
    
    public final String name;
    
    private MENU_GROUP(String name){
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
    
    public static MENU_GROUP defaultGroup() {
        return LEFT;
    }
}
