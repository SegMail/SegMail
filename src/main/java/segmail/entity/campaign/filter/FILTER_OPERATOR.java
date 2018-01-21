/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign.filter;

/**
 *
 * @author LeeKiatHaw
 */
public enum FILTER_OPERATOR {
    EQUALS("EQUALS"),
    NOT_EQUALS("NOT_EQUALS");
    
    public final String name;
    
    private FILTER_OPERATOR(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
