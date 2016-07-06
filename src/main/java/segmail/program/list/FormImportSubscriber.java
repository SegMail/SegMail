/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormImportSubscriber")
public class FormImportSubscriber {
    
    @Inject ProgramList program;
    
    @EJB SubscriptionService subService;
    
    // File upload
    private Part file;
    private String fileFirstLine;
    private List<String> fileColumns;
    private boolean renderFieldSelector;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            initMapping();
            this.setRenderFieldSelector(false);
        }
    }

    public Map<Integer, String> getListFieldMapping() {
        return program.getListFieldMapping();
    }

    public void setListFieldMapping(Map<Integer, String> listFieldMapping) {
        program.setListFieldMapping(listFieldMapping);
    }
    
    public List<SubscriptionListField> getListFields() {
        return program.getFieldList(); //Assuming that FormListFieldSet has already loaded it
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getFileFirstLine() {
        return fileFirstLine;
    }

    public void setFileFirstLine(String fileFirstLine) {
        this.fileFirstLine = fileFirstLine;
    }

    public List<String> getFileColumns() {
        return fileColumns;
    }

    public void setFileColumns(List<String> fileColumns) {
        this.fileColumns = fileColumns;
    }

    public boolean isRenderFieldSelector() {
        return renderFieldSelector;
    }

    public void setRenderFieldSelector(boolean renderFieldSelector) {
        this.renderFieldSelector = renderFieldSelector;
    }
    
    public void startImport() {
        InputStream is = null;
        try {
            Map<Integer,String> mapping = getListFieldMapping();
            is = getFile().getInputStream();
        } catch (IOException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } finally {
            try {
                if(is != null) is.close();
            } catch (IOException ex) {
                FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
            }
        }
    }
    
    public void initMapping() {
        program.setListFieldMapping(new HashMap<Integer,String>());
    }
    
    public void setColumns() {
        String[] columns = this.getFileFirstLine().split(",");
        setFileColumns(new ArrayList());
        for(String col : columns) {
            getFileColumns().add(col);
        }
        setRenderFieldSelector(true);
    }
}
