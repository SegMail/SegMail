/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import seca2.component.data.DBConnectionException;
import seca2.component.data.HibernateEMServices;
import seca2.program.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
public class FormTestDB implements Serializable {
    
    @EJB HibernateEMServices hibernateDBServices;
    
    private final String TestGenerateDBFormName = "setupDBForm";
    
    public void generateDBEM(){
        try{
            EntityManager em = hibernateDBServices.getEM();
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_INFO, "Success!",null);
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
    }
    
    public void generateDBSession(){
        try {
            Session session = hibernateDBServices.getSession();
            new SchemaExport(hibernateDBServices.createFullConfig()).create(true,true);
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_INFO, "Success!",null);
        } catch (DBConnectionException dbcex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", dbcex.getMessage());
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
        
    }
}
