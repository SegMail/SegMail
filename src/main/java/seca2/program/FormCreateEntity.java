/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
public interface FormCreateEntity {
    
    public void createNew();
    
    public void cancel();
    
    
}
