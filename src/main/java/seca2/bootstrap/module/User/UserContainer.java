/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import seca2.entity.user.User;

/**
 * Container for retrieving user info and setting user parameters.
 * Does not reveal the underlying User object.
 * 
 * @author LeeKiatHaw
 */
@SessionScoped
public class UserContainer implements Serializable{
    
    private String userId;
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    
}
