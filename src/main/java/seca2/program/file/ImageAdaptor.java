/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.file;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("ImageAdaptor")
public class ImageAdaptor {
    
    /**
     * Translates a logical address into a physical one.
     * 
     * @param logicalAddress
     * @return 
     */
    public String getImageAddress(String logicalAddress){
        //Initialize the application context path
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        
        //Let's assume that at this moment, the logicalAddress is actually a physical one
        return ec.getRequestContextPath() + logicalAddress;
    }
}
