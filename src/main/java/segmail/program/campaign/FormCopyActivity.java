/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.data.EntityNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.campaign.CampaignService;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCopyActivity")
public class FormCopyActivity {
    
    @Inject ProgramCampaign program;
    
    @EJB CampaignService campService;
    
    private String activityName;
    
    private String activityGoals;
    
    public boolean renderThis() {
        return (program.getEditingActivity() != null);
    }

    public String getActivityName() {
        //return activityName;
        return program.getEditingActivity().getACTIVITY_NAME(); //Trick our getter to retrieve the existing copy
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityGoals() {
        //return activityGoals;
        return program.getEditingActivity().getACTIVITY_GOALS(); //Trick our getter to retrieve the existing copy
    }

    public void setActivityGoals(String activityGoals) {
        this.activityGoals = activityGoals;
    }
    
    public void copy() {
        
        try {
            campService.copyCampaign(program.getEditingActivity().getOBJECTID(),
                    this.activityName,  //Careful not to use getter here because we will get the old copy!
                    this.activityGoals);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Activity copied to "+this.activityName, "");
            program.refresh();
        } catch (InstantiationException ex) {
            Logger.getLogger(FormCopyActivity.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FormCopyActivity.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(FormCopyActivity.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(FormCopyActivity.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FormCopyActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
