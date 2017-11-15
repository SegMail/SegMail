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
    NEW("NEW",0),
    EDITING("EDITING",1),
    EXECUTING("EXECUTING",2),
    COMPLETED("COMPLETED",3),
    STOPPED("STOPPED",4),
    SUSPENDED("SUSPENDED",5);
    
    public final String name;
    
    public final int order;
    
    private ACTIVITY_STATUS(String name, int order) {
        this.name = name;
        this.order = order;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
