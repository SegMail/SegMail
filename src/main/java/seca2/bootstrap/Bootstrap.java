/**
 * This is the most important class in the entire application!!! It is a 
 * core that dispatches, load and manage key components of the application.
 * <ul>
 * <li>User Management</li>
 * <li>Program Management</li>
 * <li>Presentation Management</li>
 * </ul>
 * It is important that these parts operate independently of each other and they
 * can be changed/enhanced without having to change the others.
 * 
 */

package seca2.bootstrap;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import com.ocpsoft.pretty.faces.annotation.URLBeanName;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Navigation.NavigationModule;

/**
 *
 * @author vincent.a.lee
 */
@URLMappings(mappings={
    @URLMapping(id="home", pattern="/",viewId="/program/index.xhtml"),
    @URLMapping(id="program", pattern="/program/#{bootstrap.program}/",viewId="/program/index.xhtml"),
    @URLMapping(id="install", pattern="/install/",viewId="/program/programs/install/install.xhtml")
})
@URLBeanName("bootstrap")
@Named("bootstrap")
@RequestScoped
public class Bootstrap implements Serializable {
    
    /**
     * Use this for anything. Anything!
     */
    private Map<String,Object> output;
    private Map<String,Object> input;
    /**
     * Chain of Responsibility object that will execute the modules in sequence.
     */
    @Inject private BootstrappingChainFactory bootstrappingChain;
    /**
     * Injected view parameters
     */
    private String program; //which program are you loading
    
    @PostConstruct
    public void init(){
        //Start the chain!
        BootstrapModule head = this.bootstrappingChain.getHead();
        
        System.out.println(program);
    }
    
    @URLActions(actions={
        @URLAction(mappingId="home", onPostback=false),
        @URLAction(mappingId="program", onPostback=false)
    })
    public void loadProgram(){
        System.out.println(program);
    }
    

    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Map<String, Object> getOutput() {
        return output;
    }
    
    public void setOutput(Map<String, Object> output) {    
        this.output = output;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
    // </editor-fold>
}
