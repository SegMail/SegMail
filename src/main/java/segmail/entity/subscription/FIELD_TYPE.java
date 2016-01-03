/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

/**
 *
 * @author LeeKiatHaw
 */
public enum FIELD_TYPE {
    TEXT("TEXT"),
    EMAIL("EMAIL");
    
    final String name;
    
    private FIELD_TYPE(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
