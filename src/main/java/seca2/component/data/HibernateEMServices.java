/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author KH
 */
@Stateless
public class HibernateEMServices implements Serializable {

    @PersistenceUnit(unitName="HIBERNATE")
    private EntityManagerFactory emf;
    
    @Inject private EntityExplorer entityExplorer;
    
    public EntityManager getEM() {
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("HIBERNATE");//, this.createFullConfig().getProperties());
        EntityManager em = emf.createEntityManager();
        
        return em;
    }
    
    /**
     * To be deprecated.
     * @return 
     */
    public Session getSession() throws DBConnectionException{
        Session session;
        try{
            Configuration config = this.createFullConfig();
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder ().applySettings(config.getProperties()).build();
            SessionFactory sessionFactory = config.buildSessionFactory(serviceRegistry);
            session = sessionFactory.openSession();
        }
        catch(JDBCConnectionException tdbccex){
            throw new DBConnectionException("Unable to connect to DB!");
        }
        
        
        return session;
    }
    
    public Configuration createFullConfig(){
        Configuration cfg = createPartialConfig();
        /*cfg.setProperty("hibernate.current_session_context_class", "thread");
        cfg.setProperty("hibernate.c3p0.min_size","5");
        cfg.setProperty("hibernate.c3p0.max_size","20");
        cfg.setProperty("hibernate.c3p0.timeout","30");
        cfg.setProperty("hibernate.c3p0.max_statements","100");
        cfg.setProperty("hibernate.hbm2ddl.auto","update");
        cfg.setProperty("hibernate.archive.autodetection","class, hbm");
        cfg.setProperty("hibernate.show_sql","true");
        cfg.setProperty("hibernate.connection.autocommit","false");*/
        
        cfg.configure();
        
        
        return cfg;
    }
    
    public Configuration createPartialConfig(){
        Configuration cfg = new Configuration();
        return cfg;
    }
    
}
