/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.rewrite;

import org.ocpsoft.logging.Logger.Level;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;

/**
 *
 * @author LeeKiatHaw
 */
@RewriteConfiguration
public class ApplicationConfigurationProvider extends HttpConfigurationProvider {

    @Override
    public Configuration getConfiguration(ServletContext context) {
        return ConfigurationBuilder.begin()
                .addRule()
                .perform(Log.message(Level.INFO, "Rewrite is active."))
                //.addRule()
                //.when(Path.matches("/program/{program}"))
                //.perform(Forward.to("/program/{program}/"))
                //Tough problem to solve...not a simple solution
                ;
      
    }

    @Override
    public int priority() {
        return 0;
    }
    
}
