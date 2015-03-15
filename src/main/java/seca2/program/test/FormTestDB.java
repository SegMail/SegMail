/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import eds.component.data.DBConnectionException;
import eds.utilities.EntityExplorer;
import eds.component.data.HibernateEMServices;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@RequestScoped
public class FormTestDB implements Serializable {
    
    @EJB private HibernateEMServices hibernateDBServices;
    
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
            Configuration cfg = hibernateDBServices.createFullConfig();
            
            //add all entity packages
            eds.utilities.Package root = new eds.utilities.Package();
            root.push("eds").push("entity");

            ClassLoader loader = EntityExplorer.getClassLoader();
            List<Class> entities = EntityExplorer.collectEntities(root, loader);
            for(Class c : entities){
                cfg.addAnnotatedClass(c);
            }
            //Delete all tables first
            new SchemaExport(cfg).drop(true, true);
                    //.setProperty("hibernate.hbm2ddl.auto", "create")) //it is currently update
                    //.execute(true, true, true, false);
            new SchemaExport(cfg)
                    .execute(true, true, false, true);
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_FATAL, "Success!",null);
        } catch (DBConnectionException dbcex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", dbcex.getMessage());
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
        
    }
}
