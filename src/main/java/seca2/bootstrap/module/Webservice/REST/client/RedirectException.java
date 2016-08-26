/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.REST.client;

/**
 *
 * @author LeeKiatHaw
 */
public class RedirectException extends Exception {

    public RedirectException(String redirectLink) {
        super(redirectLink);
    }
    
}
