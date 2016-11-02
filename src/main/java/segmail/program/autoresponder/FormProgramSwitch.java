/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import eds.component.GenericObjectService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.program.autoresponder.webservice.AutoresponderSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormProgramSwitch")
public class FormProgramSwitch {
    
    @Inject ProgramAutoresponder program;
    
    @Inject
    private UserSessionContainer userContainer;
    @Inject
    private UserRequestContainer requestContainer;
    @Inject
    private AutoresponderSessionContainer autoemailCont;
    
    @EJB
    private AutoresponderService autoresponderService;
    @EJB
    private GenericObjectService objectService;
    
    private String activate = "";
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadTemplate();
        }
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }
    
    public void loadTemplate() {
        List<String> params = requestContainer.getPathParser().getOrderedParams();
        if (params == null || params.isEmpty()) {
            setEditingTemplateId(-1);
            setEditingTemplate(null);
            program.setEdit(false);
            requestContainer.setRenderPageToolbar(true);
            requestContainer.setRenderPageBreadCrumbs(true);
            return;
        }
        
        String firstParam = params.get(0);
        long editingId = Long.parseLong(firstParam);
        
        AutoresponderEmail editing = objectService.getEnterpriseObjectById(editingId, AutoresponderEmail.class);
        setEditingTemplate(editing);
        setEditingTemplateId(editingId);
        program.setEdit(true);
        requestContainer.setRenderPageToolbar(false);
        requestContainer.setRenderPageBreadCrumbs(false);
    }
    
    public boolean isEdit() {
        return program.isEdit();
    }

    public void setEdit(boolean edit) {
        program.setEdit(edit);
    }
    
    public AutoresponderEmail getEditingTemplate() {
        return program.getEditingTemplate();
    }

    public void setEditingTemplate(AutoresponderEmail editingTemplate) {
        program.setEditingTemplate(editingTemplate);
        autoemailCont.setEditingTemplate(editingTemplate);
    }
    
    public long getEditingTemplateId() {
        return program.getEditingTemplateId();
    }

    public void setEditingTemplateId(long editingTemplateId) {
        program.setEditingTemplateId(editingTemplateId);
    }
}
