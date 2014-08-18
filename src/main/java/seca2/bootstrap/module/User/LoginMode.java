/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.User;

/**
 * This is a temporary arrangement, while the persistence version is being developed
 * so that users can change the login mode from the frontend.
 * @author KH
 */
public enum LoginMode {
    BLOCK ("BLOCK","/programs/user/login_block.xhtml"),
    PAGE ("PAGE","/programs/user/login_page.xhtml");
    //INTEGRATED ("INTEGRATED","");
    
    public final String name;
    public final String include;
    
    LoginMode(String name, String include){
        this.name = name;
        this.include = include;
    }
    
}
