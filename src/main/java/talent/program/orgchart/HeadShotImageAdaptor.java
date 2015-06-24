package talent.program.orgchart;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
    
    private String directory;
    
    @PostConstruct
    public void init(){
        //Initialize the application context path
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        directory = ec.getRequestContextPath() /*+ ec.getRequestServletPath()*/ + "/programs/orgchart/images/HS";
    }

    public String getDirectory() {
        return directory;
    }
    
    
}
