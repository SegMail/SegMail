/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import com.amazonaws.auth.BasicAWSCredentials;

/**
 *
 * @author LeeKiatHaw
 */
@Password
public class AWSPassword extends BasicAWSCredentials {
    
    /**
     * This is "Segmail-test-macbook-1"
     */
    private static final String AWS_ACCESS_KEY_ID = "AKIAJLE37WT3UJIL3VEQ";
    private static final String AWS_SECRET_ACCESS_KEY = "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";

    public AWSPassword() {
        super(
                System.getProperty("AWSPassword.AWS_ACCESS_KEY_ID",AWS_ACCESS_KEY_ID),
                System.getProperty("AWSPassword.AWS_SECRET_ACCESS_KEY",AWS_SECRET_ACCESS_KEY)
        );
        
    }
    
}
