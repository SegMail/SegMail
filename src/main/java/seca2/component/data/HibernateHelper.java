/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.data;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

/**
 *
 * @author LeeKiatHaw
 */
public class HibernateHelper {

    /**
     * From http://stackoverflow.com/questions/2216547/converting-hibernate-proxy-to-real-object
     * This is a method to convert all lazy loaded entity properties into the 
     * required class
     * 
     * @param <T>
     * @param entity
     * @return 
     */
    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }
}
