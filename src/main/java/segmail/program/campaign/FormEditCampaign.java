/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.program.FormEditEntity;
import segmail.entity.campaign.Campaign;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditCampaign")
public class FormEditCampaign implements FormEditEntity  {
    
    @Inject ProgramCampaign program;
    
    public Campaign getEditingCampaign() {
        return program.getEditingCampaign();
    }

    public void setEditingCampaign(Campaign editingCampaign) {
        program.setEditingCampaign(editingCampaign);
    }

    @Override
    public void saveAndContinue() {
        
    }

    @Override
    public void saveAndClose() {
        saveAndContinue();
        saveAndClose();
    }

    @Override
    public void closeWithoutSaving() {
        program.refresh();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
