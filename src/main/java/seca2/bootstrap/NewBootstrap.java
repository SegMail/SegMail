/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 *
 * @author LeeKiatHaw
 */
@RewriteConfiguration
public class NewBootstrap extends HttpConfigurationProvider {

    @Override
    public Configuration getConfiguration(ServletContext t) {
        return ConfigurationBuilder.begin()
                
                .addRule(Join.path("/").to("/program/index.xhtml"))
                
                .addRule(Join.path("/program/#{program}/").to("/program/index.xhtml"))
                
                ;
    }

    @Override
    public int priority() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
