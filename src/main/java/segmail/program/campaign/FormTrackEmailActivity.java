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
import segmail.entity.campaign.CampaignActivity;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormTrackEmailActivity")
public class FormTrackEmailActivity implements FormEditEntity  {
    
    @Inject ProgramCampaign program;
    
    public CampaignActivity getEditingActivity() {
        return program.getEditingActivity();
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        program.setEditingActivity(editingActivity);
    }

    @Override
    public void saveAndContinue() {
        
    }

    @Override
    public void saveAndClose() {
        this.saveAndContinue();
        this.closeWithoutSaving();
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
