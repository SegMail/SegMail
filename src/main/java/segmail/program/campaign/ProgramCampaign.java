/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.util.List;
import javax.inject.Named;
import seca2.program.Program;

/**
 *
 * @author Administrator
 */
@Named("ProgramCampaign")
public class ProgramCampaign extends Program{

    private boolean editCampaignMode = false; //Determines whether to show the list of campaign or an individual campaign.
    
    @Override
    public void clearVariables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initProgramParams() {
        this.initEditCampaignMode(); //On first load
    }

    @Override
    public void initProgram() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isEditCampaignMode() {
        return editCampaignMode;
    }

    public void setEditCampaignMode(boolean editCampaignMode) {
        this.editCampaignMode = editCampaignMode;
    }
    
    public void initEditCampaignMode() {
        //Initialize the campaign ID, if exist and decide whether to load the campaign or not.
        List<String> params = reqContainer.getProgramParamsOrdered();
        if(params != null && !params.isEmpty())
            this.setEditCampaignMode(true);
    }
    
}
