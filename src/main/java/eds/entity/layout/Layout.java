/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.layout;

import eds.entity.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="LAYOUT")
@EntityListeners(LayoutListener.class)
public class Layout extends EnterpriseObject {

    private String LAYOUT_NAME;
    private String VIEW_ROOT;

    public String getLAYOUT_NAME() {
        return LAYOUT_NAME;
    }

    public void setLAYOUT_NAME(String LAYOUT_NAME) {
        this.LAYOUT_NAME = LAYOUT_NAME;
    }

    public String getVIEW_ROOT() {
        return VIEW_ROOT;
    }

    public void setVIEW_ROOT(String VIEW_ROOT) {
        this.VIEW_ROOT = VIEW_ROOT;
    }
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
