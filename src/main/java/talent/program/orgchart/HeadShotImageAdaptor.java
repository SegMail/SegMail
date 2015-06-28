package talent.program.orgchart;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("HeadShotImageAdaptor")
public class HeadShotImageAdaptor {
    
    @Inject private ProgramOrgChart program;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            initDirectory();
        }
    }

    public void initDirectory(){
        //Initialize the application context path
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        program.setHSDirectory(ec.getRequestContextPath() /*+ ec.getRequestServletPath()*/ + "/programs/orgchart/images/HS");
    }
    
    
}
