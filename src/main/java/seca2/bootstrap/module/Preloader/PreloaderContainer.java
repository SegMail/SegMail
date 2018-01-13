/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Preloader;

import eds.entity.layout.Layout;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("PreloaderContainer")
public class PreloaderContainer {
    
    private String templateLocation;
    
    private Layout layout;
    
    private String preloadMessageMain;
    
    private String preloadMessageSub;
    
    private int timeout;
    
    private boolean render;
    
    @PostConstruct
    public void init() {
        timeout = 1000; //default 2s
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public String getPreloadMessageMain() {
        return preloadMessageMain;
    }

    public void setPreloadMessageMain(String preloadMessageMain) {
        this.preloadMessageMain = preloadMessageMain;
    }

    public String getPreloadMessageSub() {
        return preloadMessageSub;
    }

    public void setPreloadMessageSub(String preloadMessageSub) {
        this.preloadMessageSub = preloadMessageSub;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }
}
