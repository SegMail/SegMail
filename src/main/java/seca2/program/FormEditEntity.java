/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import eds.component.data.EntityNotFoundException;
import javax.faces.application.FacesMessage;
import seca2.entity.landing.ServerResource;
import seca2.entity.landing.ServerResourceType;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
public interface FormEditEntity {
    
    public void saveAndContinue();
    
    public void saveAndClose();
    
    public void closeWithoutSaving();
    
    public void delete();
}
