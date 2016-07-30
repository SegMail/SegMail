/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

/**
 *
 * @author LeeKiatHaw
 */
public enum ACTIVITY_STATUS {
    NEW("NEW"),
    EDITING("EDITING"),
    EXECUTING("EXECUTING"),
    COMPLETED("COMPLETED"),
    STOPPED("STOPPED");
    
    public final String name;
    
    private ACTIVITY_STATUS(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}
