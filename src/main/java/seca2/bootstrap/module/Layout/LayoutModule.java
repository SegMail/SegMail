/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Layout;

import eds.component.data.DBConnectionException;
import eds.component.layout.LayoutService;
import eds.entity.data.EnterpriseObject;
import eds.entity.layout.Layout;
import eds.entity.layout.LayoutAssignment;
import eds.entity.user.User;
import eds.entity.user.UserType;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.module.User.UserContainer;

/**
 *
 * @author vincent.a.lee
 */
@CoreModule
public class LayoutModule extends BootstrapModule implements Serializable {

    @EJB
    private LayoutService layoutService;

    @Inject
    private UserContainer userContainer;
    //Hard code only 1 template at the moment, we will build this entire module later!
    //private final String DEFAULT_TEMPLATE_LOCATION = "/templates/mytemplate/template-layout.xhtml";
    //private final String DEFAULT_TEMPLATE_LOCATION = "/templates/beprobootstrap/template-layout.xhtml";
    private String DEFAULT_TEMPLATE_LOCATION;

    @PostConstruct
    public void init() {
        DEFAULT_TEMPLATE_LOCATION = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("DEFAULT_TEMPLATE_LOCATION");
    }

    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        try {

            //1st priority is Program, if can find, return it first
            String program = inputContext.getProgram();
            Layout layout = this.layoutService.getLayoutAssignmentsByProgram(program);
            if (layout != null) {
                outputContext.setTemplateRoot(layout.getVIEW_ROOT());
                return true;
            }

            //Next priority is User and UserType
            if (userContainer == null) {
                outputContext.setTemplateRoot(this.DEFAULT_TEMPLATE_LOCATION);
                return true;
            }

            if (!userContainer.isLoggedIn()) {
                outputContext.setTemplateRoot(this.DEFAULT_TEMPLATE_LOCATION);
                return true;
            }

            if (userContainer.getUser() == null) {
                outputContext.setTemplateRoot(this.DEFAULT_TEMPLATE_LOCATION);
                return true;
            }

            if (userContainer.getUserType() == null) {
                outputContext.setTemplateRoot(this.DEFAULT_TEMPLATE_LOCATION);
                return true;
            }
            List<LayoutAssignment> assignments = layoutService.getLayoutAssignmentsByUser(userContainer.getUser());
            for (LayoutAssignment assignment : assignments) {
                EnterpriseObject target = assignment.getTARGET();
                if (target instanceof User) {
                    layout = (Layout) assignment.getSOURCE();
                    outputContext.setTemplateRoot(layout.getVIEW_ROOT());
                    return true;
                }
            }

            for (LayoutAssignment assignment : assignments) {
                EnterpriseObject target = assignment.getTARGET();
                if (target instanceof UserType) {
                    layout = (Layout) assignment.getSOURCE();
                    outputContext.setTemplateRoot(layout.getVIEW_ROOT());
                    return true;
                }

            }
            outputContext.setTemplateRoot(this.DEFAULT_TEMPLATE_LOCATION);

            return true;

        } catch (DBConnectionException ex) {
            outputContext.setTemplateRoot(this.DEFAULT_TEMPLATE_LOCATION);
            return true;
        }
    }

    @Override
    protected int executionSequence() {
        return -96;
    }

    @Override
    protected boolean inService() {
        return true;
    }

    private boolean canSkip(BootstrapInput inputContext, BootstrapOutput outputContext) {
        //If the following conditions are met:
        //- Previous request and current request are the same,
        boolean sameRequest = (userContainer.getLastProgram() == null) ? false
                : userContainer.getLastProgram().equalsIgnoreCase(inputContext.getProgram());
        //- UserContainer isLoggedIn(),
        boolean isLoggedIn = userContainer.isLoggedIn();
        //- PageRoot is not empty
        boolean templateRootExists = (outputContext.getTemplateRoot() == null) ? false
                : (!outputContext.getTemplateRoot().isEmpty());
        //then skip processing
        return sameRequest && isLoggedIn && templateRootExists;
    }

}
