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

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author vincent.a.lee
 */
//@URLMappings(mappings={
//    @URLMapping(id="home", pattern="/",viewId="/program/index.xhtml"),
//    @URLMapping(id="program", pattern="/program/#{bootstrap.program}/",viewId="/program/index.xhtml"),
//    @URLMapping(id="install", pattern="/install/",viewId="/program/programs/install/install.xhtml")
//})
//@URLBeanName("bootstrap")
//@Named("bootstrap")
//@RequestScoped
@WebListener
public class Bootstrap implements Serializable, ServletContextListener {
    
    /**
     * Chain of Responsibility object that will execute the modules in sequence.
     */
    @Inject private BootstrappingChainFactory bootstrappingChain;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        ServletContext ctx = sce.getServletContext();
        
        List<BootstrapModule> modules = bootstrappingChain.getAllBootstrapModuleList();
        for(BootstrapModule bm : modules){
            FilterRegistration fr = ctx.addFilter(bm.getName(), bm.getClass());
            fr.addMappingForUrlPatterns(
                    EnumSet.copyOf(bm.getDispatchTypes()),
                    true, 
                    bm.urlPattern()
            );
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }


}
