/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Administrator
 */
@RequestScoped
@Named("FormProgramModeSwitch")
public class FormProgramModeSwitch {
    
    @Inject ProgramCampaign program;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback())
            program.initEditCampaignMode();
    }
    
    public boolean isEditCampaignMode() {
        return program.isEditCampaignMode();
    }

    public void setEditCampaignMode(boolean editCampaignMode) {
        program.setEditCampaignMode(editCampaignMode);
    }
}
