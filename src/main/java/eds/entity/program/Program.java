/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.program;


import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Not needed at this moment, because each MenuItem can point to the program's
 * page, no need to instantiate a Program entity.
 * 
 * @author vincent.a.lee
 */
@Entity
@Table(name="PROGRAM")
public class Program extends EnterpriseObject{
    
    private String PROGRAM_NAME;
    //private String VIEW_DIRECTORY;
    private String VIEW_ROOT;
    //private String BEAN_DIRECTORY;
    
    private String DISPLAY_TITLE;
    private String DISPLAY_DESCRIPTION;
    
    private Boolean IS_PUBLIC; //http://stackoverflow.com/a/10648207/5765606
    
    private boolean RENDER_PRELOADER;
    
    public Program() {
        super();
    }
    
    /**
     * Copy constructor
     * @param program 
     */
    public Program(Program program) {
        this.PROGRAM_NAME = program.PROGRAM_NAME;
        //this.VIEW_DIRECTORY = program.VIEW_DIRECTORY;
        this.VIEW_ROOT = program.VIEW_ROOT;
        //this.BEAN_DIRECTORY = program.BEAN_DIRECTORY;
        this.DISPLAY_TITLE = program.DISPLAY_TITLE;
        this.DISPLAY_DESCRIPTION = program.DISPLAY_DESCRIPTION;
        this.IS_PUBLIC = program.IS_PUBLIC;
    }
    
    public String getPROGRAM_NAME() {
        return PROGRAM_NAME;
    }

    public void setPROGRAM_NAME(String PROGRAM_NAME) {
        this.PROGRAM_NAME = PROGRAM_NAME;
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

    public boolean isIS_PUBLIC() {
        return (IS_PUBLIC == null) ? false : this.IS_PUBLIC;
    }

    public void setIS_PUBLIC(Boolean IS_PUBLIC) {
        this.IS_PUBLIC = IS_PUBLIC;
    }

    public boolean isRENDER_PRELOADER() {
        return RENDER_PRELOADER;
    }

    public void setRENDER_PRELOADER(boolean RENDER_PRELOADER) {
        this.RENDER_PRELOADER = RENDER_PRELOADER;
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
