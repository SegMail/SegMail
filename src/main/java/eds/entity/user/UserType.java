/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.user;

import eds.entity.EnterpriseObject;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="USERTYPE")
@DiscriminatorValue("USERTYPE")
public class UserType extends EnterpriseObject {
    
    private String USERTYPENAME;
    private String DESCRIPTION;
    
    /**
     * Not in use yet
     */
    private boolean PORTAL_ACCESS;
    private boolean WS_ACCESS;

    public String getUSERTYPENAME() {
        return USERTYPENAME;
    }

    public void setUSERTYPENAME(String USERTYPENAME) {
        this.USERTYPENAME = USERTYPENAME;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public boolean isPORTAL_ACCESS() {
        return PORTAL_ACCESS;
    }

    public void setPORTAL_ACCESS(boolean PORTAL_ACCESS) {
        this.PORTAL_ACCESS = PORTAL_ACCESS;
    }

    public boolean isWS_ACCESS() {
        return WS_ACCESS;
    }

    public void setWS_ACCESS(boolean WS_ACCESS) {
        this.WS_ACCESS = WS_ACCESS;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @PrePersist
    @PreUpdate
    public void PrePersist(){
        this.OBJECT_NAME = this.USERTYPENAME;
    }
}
