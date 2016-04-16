/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.user;

/**
 * DO NOT ANNOTATED THIS EXCEPTION WITH @ApplicationException(rollback = true)
 * BECAUSE IF USER CANNOT LOG IN, THE FAILED ATTEMPT IS SUPPOSED TO BE COMMITTED
 * INTO THE DB.
 * 
 * @author LeeKiatHaw
 */
public class UserLoginException extends Exception {

    public UserLoginException(String message) {
        super(message);
    }
}
