/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.user;

/**
 *
 * @author LeeKiatHaw
 */
public class UserNotFoundException extends Exception {
    
    private long userid;
    private String username;
    
    private static final String message = "User not found.";

    public UserNotFoundException(long id) {
        super(message);
        this.setUserid(id); //Some applications would not want to show the userid to the client
    }
    
    public UserNotFoundException(String username) {
        super(message);
        this.setUsername(username); //Some applications would not want to show the userid to the client
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    
    
    
}
