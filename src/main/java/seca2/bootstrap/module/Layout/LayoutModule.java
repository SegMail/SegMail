/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.Layout;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;

/**
 *
 * @author vincent.a.lee
 */
@CoreModule
public class LayoutModule extends BootstrapModule implements Serializable {

    //Hard code only 1 template at the moment, we will build this entire module later!
    //private final String DEFAULT_TEMPLATE_LOCATION = "/templates/mytemplate/template-layout.xhtml";
    //private final String DEFAULT_TEMPLATE_LOCATION = "/templates/beprobootstrap/template-layout.xhtml";
    private String DEFAULT_TEMPLATE_LOCATION;
    
    @PostConstruct
    public void init(){
        DEFAULT_TEMPLATE_LOCATION = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("DEFAULT_TEMPLATE_LOCATION");
    }
    
    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        
        //Always set the default template location for now
        outputContext.setTemplateRoot(this.DEFAULT_TEMPLATE_LOCATION);
        
        return true;
    }

    @Override
    protected int executionSequence() {
        return -96;
    }

    @Override
    protected boolean inService() {
        return true;
    }
    
}
