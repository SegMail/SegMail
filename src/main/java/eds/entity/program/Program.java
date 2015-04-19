/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.program;


import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Not needed at this moment, because each MenuItem can point to the program's
 * page, no need to instantiate a Program entity.
 * 
 * @author vincent.a.lee
 */
@Entity
@Table(name="PROGRAM")
@TableGenerator(name="PROGRAM_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
@EntityListeners(ProgramListener.class)
public class Program extends EnterpriseObject{
    
    private String PROGRAM_NAME;
    private String VIEW_DIRECTORY;
    private String VIEW_ROOT;
    private String BEAN_DIRECTORY;
    
    private String DISPLAY_TITLE;
    private String DISPLAY_DESCRIPTION;
    
    public String getPROGRAM_NAME() {
        return PROGRAM_NAME;
    }

    public void setPROGRAM_NAME(String PROGRAM_NAME) {
        this.PROGRAM_NAME = PROGRAM_NAME;
    }

    public String getVIEW_DIRECTORY() {
        return VIEW_DIRECTORY;
    }

    public void setVIEW_DIRECTORY(String VIEW_DIRECTORY) {
        this.VIEW_DIRECTORY = VIEW_DIRECTORY;
    }

    public String getBEAN_DIRECTORY() {
        return BEAN_DIRECTORY;
    }

    public void setBEAN_DIRECTORY(String BEAN_DIRECTORY) {
        this.BEAN_DIRECTORY = BEAN_DIRECTORY;
    }

    public String getVIEW_ROOT() {
        return VIEW_ROOT;
    }

    public void setVIEW_ROOT(String VIEW_ROOT) {
        this.VIEW_ROOT = VIEW_ROOT;
    }

    public String getDISPLAY_TITLE() {
        return DISPLAY_TITLE;
    }

    public void setDISPLAY_TITLE(String DISPLAY_TITLE) {
        this.DISPLAY_TITLE = DISPLAY_TITLE;
    }

    public String getDISPLAY_DESCRIPTION() {
        return DISPLAY_DESCRIPTION;
    }

    public void setDISPLAY_DESCRIPTION(String DISPLAY_DESCRIPTION) {
        this.DISPLAY_DESCRIPTION = DISPLAY_DESCRIPTION;
    }
    
    

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String alias() {
        return this.PROGRAM_NAME;
    }

    
}
