/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component;

import java.sql.Date;
import javax.ejb.Stateless;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
public abstract class Service {
    
    protected java.sql.Date TAP_START;
    protected java.sql.Date TAP_END;
    
    public Service(Date TAP_START, Date TAP_END) {
        this.TAP_START = TAP_START;
        this.TAP_END = TAP_END;
    }
    
    public Service(){
        //by default, all dates are today
        DateTime today = new DateTime();
        java.sql.Date todaySQL = new java.sql.Date(today.getMillis());
        
        this.TAP_START = (java.sql.Date)todaySQL.clone();
        this.TAP_END = (java.sql.Date)todaySQL.clone();
    }

    
    
}
