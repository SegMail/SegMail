/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.user;

import seca2.entity.user.User;
import seca2.entity.user.UserPreferenceSet;

/**
 *
 * @author LeeKiatHaw
 */
public class UserContainer {
    
    private User user;
    private UserPreferenceSet preferences;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserPreferenceSet getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferenceSet preferences) {
        this.preferences = preferences;
    }
    
    
}
