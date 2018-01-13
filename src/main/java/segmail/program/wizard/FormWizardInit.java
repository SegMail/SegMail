/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import eds.component.client.ClientAWSService;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormWizardInit")
public class FormWizardInit {
    
    @Inject ProgramSetupWizard program;
    
    @Inject UserRequestContainer reqCont;
    @Inject UserSessionContainer sessCont;
    
    @EJB ClientAWSService cltService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            initPageToolbar();
            gotoStage(0);
        }
    }
    
    public String getLastStage() {
        return program.getLastStage();
    }

    public void setLastStage(String lastStage) {
        program.setLastStage(lastStage);
    }
    
    
    public int getCurrentStage() {
        return program.getCurrentStage();
    }

    public void setCurrentStage(int currentStage) {
        program.setCurrentStage(currentStage);
    }
    
    /**
     * Might not use
     */
    public void gotoLast() {
        setLastStage(program.getStages().get(0));
        setCurrentStage(0);
        for(int i : program.getStagesMap().keySet()) {
            Map<String,Object> map = program.getStagesMap().get(i);
            if(!(boolean)map.get(program.getKEY_COMPLETED())) {
                return;
            }
            setLastStage((String) map.get(program.getKEY_NAME()));
            setCurrentStage(i);
        }
    }
    
    public void initPageToolbar() {
        reqCont.setRenderPageToolbar(false);
        reqCont.setRenderPageBreadCrumbs(false); 
    }
    
    public List<String> getStages() {
        return program.getStages();
    }

    public void setStages(List<String> stages) {
        program.setStages(stages);
    }
    
    public void gotoStage(int i) {
        if (i < 0 || i >= getStages().size()) {
            setCurrentStage(0);
        } else {
            setCurrentStage(i);
        }
        //program.refresh();
    }
    
    public void nextPage() {
        int currentPage = getCurrentStage();
        gotoStage(++currentPage);
    }
}
