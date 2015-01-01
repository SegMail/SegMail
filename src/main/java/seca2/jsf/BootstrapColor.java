/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf;

/**
 *
 * @author LeeKiatHaw
 */
public enum BootstrapColor {
    DEFAULT ("default"),
    PRIMARY ("primary"),
    SUCCESS ("success"),
    INFO ("info"),
    WARNING ("warning"),
    DANGER ("danger");
    
    private final String color;
    
    private BootstrapColor(String color){
        this.color = color;
    }
    
}
