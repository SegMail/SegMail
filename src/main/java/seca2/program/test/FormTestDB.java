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
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import eds.component.data.DBConnectionException;
import eds.utilities.EntityExplorer;
import eds.component.data.HibernateEMServices;
import java.util.ArrayList;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@RequestScoped
public class FormTestDB implements Serializable {

    @EJB
    private HibernateEMServices hibernateDBServices;

    private final String TestGenerateDBFormName = "setupDBForm";

    public void generateDBEM() {
        try {
            EntityManager em = hibernateDBServices.getEM();

            //If this works, move everything to HibernateEMServices
            Metamodel metamodel = em.getMetamodel();
            List<Class> allEntities = new ArrayList<Class>();
            for (final ManagedType<?> managedType : metamodel.getManagedTypes()) {
                allEntities.add(managedType.getJavaType()); // this returns the java class of the @Entity object
            }

            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_INFO, "Success!", null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
    }

    public void generateDBSession() {
        try {
            //Session session = hibernateDBServices.getSession();
            Configuration cfg = hibernateDBServices.createFullConfig();

            //add all entity packages
            eds.utilities.Package root = new eds.utilities.Package();
            root.push("eds").push("entity");

            ClassLoader loader = EntityExplorer.getClassLoader();
            List<Class> entities = EntityExplorer.collectEntities(root, loader);
            for (Class c : entities) {
                cfg.addAnnotatedClass(c);
            }
            //Delete all tables first
            new SchemaExport(cfg).drop(true, true);
            //.setProperty("hibernate.hbm2ddl.auto", "create")) //it is currently update
            //.execute(true, true, true, false);
            new SchemaExport(cfg)
                    .execute(true, true, true, true);
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_FATAL, "Success!", null);
        } catch (DBConnectionException dbcex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", dbcex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }

    }

    //It seems that either hibernate or JPA alone would work. Instead, both must be used together
    public void generateDB() {
        try {
            //Seems like you can't use Hibernate session methods within a CMT in an EJB
            //hibernateDBServices.regenerateDBTables();
            List<Class> allEntities = this.hibernateDBServices.getAllEntities();

            Configuration cfg = this.hibernateDBServices.createFullConfig();
            for (Class c : allEntities) {
                cfg.addAnnotatedClass(c);
            }

            //Delete all tables first
            new SchemaExport(cfg).drop(true, true);
            //.setProperty("hibernate.hbm2ddl.auto", "create")) //it is currently update
            //.execute(true, true, true, false);
            new SchemaExport(cfg)
                    .execute(true, true, false, true);
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_FATAL, "Success!", null);
        } catch (DBConnectionException dbcex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", dbcex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
    }
    
    public void updateDB() {
        try {
            //Seems like you can't use Hibernate session methods within a CMT in an EJB
            //hibernateDBServices.regenerateDBTables();
            List<Class> allEntities = this.hibernateDBServices.getAllEntities();

            Configuration cfg = this.hibernateDBServices.createFullConfig();
            for (Class c : allEntities) {
                cfg.addAnnotatedClass(c);
            }

            //Delete all tables first
            //new SchemaExport(cfg).drop(true, true);
            //.setProperty("hibernate.hbm2ddl.auto", "create")) //it is currently update
            //.execute(true, true, true, false);
            new SchemaExport(cfg)
                    .execute(true, true, false, true);
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_FATAL, "Success!", null);
        } catch (DBConnectionException dbcex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", dbcex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
    }
}
