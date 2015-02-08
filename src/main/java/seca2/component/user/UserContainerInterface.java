/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.user;

import java.util.List;
import seca2.entity.user.User;
import seca2.entity.user.UserPreferenceSet;
import seca2.entity.user.UserType;

/**
 *
 * @author LeeKiatHaw
 */
public interface UserContainerInterface {
    
    public String regenerateSessionId();

    public User getUser();

    public void setUser(User user);
    
    public UserType getUserType();

    public void setUserType(UserType userType);

    public List<UserPreferenceSet> getPreferences();

    public void setPreferences(List<UserPreferenceSet> preferences);

    public String getLastURL();

    public void setLastURL(String lastURL);

    public boolean isLoggedIn();

    public void setLoggedIn(boolean loggedIn);

    public String getSessionId();

    public void setSessionId(String sessionId);
    
}
