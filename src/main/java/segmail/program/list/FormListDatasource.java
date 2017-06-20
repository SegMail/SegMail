/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.IncompleteDataException;
import eds.entity.data.copier.GenericCopier;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.datasource.DatasourceService;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.datasource.DATASOURCE_ENDPOINT_TYPE;
import segmail.entity.subscription.datasource.ListDataMapping;
import segmail.entity.subscription.datasource.ListDatasource;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListDatasource")
@RequestScoped
public class FormListDatasource {
    
    @Inject ProgramList program;
    
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    @EJB DatasourceService dsService;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            initDatasource();
            initDataMapping();
            updateCached(); //Call this first time when loading page
            tryConnection(true);
            refreshStatusField(true);
        }
    }

    public ListDatasource getNewDatasource() {
        return program.getNewDatasource();
    }

    public void setNewDatasource(ListDatasource newDatasource) {
        program.setNewDatasource(newDatasource);
    }
    
    public List<SubscriptionListField> getFieldList() {
        return program.getFieldList();
    }

    public void setFieldList(List<SubscriptionListField> fieldList) {
        program.setFieldList(fieldList);
    }
    
    public List<ListDataMapping> getDatasourceMappings() {
        return program.getDatasourceMappings();
    }

    public void setDatasourceMappings(List<ListDataMapping> datasourceMappings) {
        program.setDatasourceMappings(datasourceMappings);
    }
    
    public List<String> getRemoteDBFields() {
        return program.getRemoteDBFields();
    }

    public void setRemoteDBFields(List<String> remoteDBFields) {
        program.setRemoteDBFields(remoteDBFields);
    }
    
    public String getConnectionString() {
        return program.getConnectionString();
    }

    public void setConnectionString(String connectionString) {
        program.setConnectionString(connectionString);
    }
    
    public String getOldPassword() {
        return program.getOldPassword();
    }

    public void setOldPassword(String oldPassword) {
        program.setOldPassword(oldPassword);
    }
    
    public boolean isUseStatusField() {
        return program.isUseStatusField();
    }

    public void setUseStatusField(boolean useStatusField) {
        program.setUseStatusField(useStatusField);
    }

    public String getStatusField() {
        return program.getStatusField();
    }

    public void setStatusField(String statusField) {
        program.setStatusField(statusField);
    }

    public List<String> getStatusFieldValues() {
        return program.getStatusFieldValues();
    }

    public void setStatusFieldValues(List<String> statusFieldValues) {
        program.setStatusFieldValues(statusFieldValues);
    }

    public String getStatusFieldValue() {
        return program.getStatusFieldValue();
    }

    public void setStatusFieldValue(String statusFieldValue) {
        program.setStatusFieldValue(statusFieldValue);
    }
    
    public List<String> getStatusFields() {
        return program.getStatusFields();
    }

    public void setStatusFields(List<String> statusFields) {
        program.setStatusFields(statusFields);
    }
    
    
    public boolean useDataSource() {
        return false;
    }
    
    public String getOldStatusField() {
        return program.getOldStatusField();
    }

    public void setOldStatusField(String oldStatusField) {
        program.setOldStatusField(oldStatusField);
    }

    public String getOldStatusFieldValue() {
        return program.getOldStatusFieldValue();
    }

    public void setOldStatusFieldValue(String oldStatusFieldValue) {
        program.setOldStatusFieldValue(oldStatusFieldValue);
    }
    
    public DATASOURCE_ENDPOINT_TYPE[] endpointTypes() {
        return DATASOURCE_ENDPOINT_TYPE.values();
    }
    
    public String getIP() {
        try {
            InetAddress address = InetAddress.getByName("admin.segmail.io");
            return address.getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(FormListDatasource.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
        return "";
    }
    
    public void initDatasource() {
        if(program.getListEditing() == null)
            return;
        
        //Retrieve the existing datasource
        List<ListDatasource> datasources = objService.getEnterpriseData(program.getListEditing().getOBJECTID(), ListDatasource.class);
        
        //If no existing, instantiate a new one
        if(datasources == null || datasources.isEmpty()) {
            program.setNewDatasource(new ListDatasource());
            //program.getNewDatasource().setOWNER(program.getListEditing());//We use this to tell late if it is new or existing
        } else {
            program.setNewDatasource(datasources.get(0));
        }
        
    }
    
    public void updateCached() {
        setOldPassword(getNewDatasource().getPASSWORD());
        setConnectionString(getNewDatasource().connectionKey());
    }
    
    public void updateDatasource(){
        try {
            //Some transaction management problem here!!
            //Put this whole chunk into an EJB
            if(program.getNewDatasource().getOWNER() == null) { //New
                program.getNewDatasource().setOWNER(program.getListEditing());
                updService.persist(program.getNewDatasource());
            } else {
                //If password is empty, use the old password.
                ListDatasource updated = program.getNewDatasource();
                if(updated.getPASSWORD() == null || updated.getPASSWORD().isEmpty()) {
                    updated.setPASSWORD(getOldPassword());
                }
                
                this.dsService.update(updated);
                //program.setNewDatasource(updated);
                
            }
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Saved!", "");
        } catch(EJBException ex){
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getCause().getMessage(), "");
        }   catch(Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } finally {
            initDatasource();
            tryConnection(false);
            refreshStatusField(false);
            this.updateCached();
        }
    }
    
    public void initDataMapping() {
        if(program.getListEditing() == null)
            return;
        
        List<SubscriptionListField> fields = getFieldList();
        List<ListDataMapping> existingMappings = objService.getEnterpriseData(program.getListEditing().getOBJECTID(), ListDataMapping.class);
        
        //If exist, update the SNO from field
        //If not, create new
        for(SubscriptionListField field : fields) {
            ListDataMapping mapping = null;
            for(ListDataMapping m : existingMappings) {
                if(m.getKEY_NAME() != null && field.generateKey() != null
                        && m.getKEY_NAME().equals(field.generateKey())) {
                    mapping = m;
                    break;
                }
            }
            if(mapping == null) {
                mapping = new ListDataMapping();
                mapping.setOWNER(program.getListEditing());
                existingMappings.add(mapping);
            }
            mapping.setSNO(field.getSNO());
            mapping.setKEY_NAME(field.generateKey().toString());
            mapping.setLOCAL_NAME(field.getFIELD_NAME());
            mapping.setTYPE(FIELD_TYPE.valueOf(field.getTYPE()));
            
            //Don't update them first, just add them to a list and sort them
        }
        
        Collections.sort(existingMappings, new Comparator<ListDataMapping>(){

            @Override
            public int compare(ListDataMapping o1, ListDataMapping o2) {
                return o1.getSNO() - o2.getSNO();
            }
            
        });
        
        setDatasourceMappings(existingMappings);
    }
    
    public void updateMappings() {
        try{
            List<ListDataMapping> refreshed = dsService.refreshDataMappings(program.getListEditing().getOBJECTID(),getDatasourceMappings());
        
            setDatasourceMappings(refreshed);
            
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Field Mapping updated!", "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
        
    }
    
    public void tryConnection(boolean force) {
        try {
            String connStringCurr = getConnectionString();
            String connStringNew = getNewDatasource().connectionKey();
            if(!force &&
                    (getConnectionString() == null || getNewDatasource().connectionKey()== null
                    || getConnectionString().equals(getNewDatasource().connectionKey()))
                    )
                return;
            
            List<String> columns = dsService.getColumns(getNewDatasource());
            
            setRemoteDBFields(columns);
        } catch (SQLException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public void refreshStatusField(boolean force) {
        
        try {
            if(!force && !isUseStatusField() &&
                    (getOldStatusField()== null || getNewDatasource().getSTATUS_FIELD() == null
                    || getOldStatusField().equals(getNewDatasource().getSTATUS_FIELD()))
                    )
                return;
            
            //Get chosen data mapping field
            String chosenStatusField = getNewDatasource().getSTATUS_FIELD();
            
            List<String> columns = dsService.getDistinctValues(getNewDatasource(),chosenStatusField);
            
            setStatusFieldValues(columns);
            
        } catch (IncompleteDataException ex) {
            Logger.getLogger(FormListDatasource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(FormListDatasource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
