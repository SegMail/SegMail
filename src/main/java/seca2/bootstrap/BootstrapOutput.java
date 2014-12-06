/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.Map;

/**
 *
 * @author LeeKiatHaw
 */
public class BootstrapOutput {
    
    private String pageRoot;
    private String templateRoot;
    
    private Map<String,Object> nonCoreValues;

    public String getPageRoot() {
        return pageRoot;
    }

    public void setPageRoot(String pageRoot) {
        this.pageRoot = pageRoot;
    }

    public String getTemplateRoot() {
        return templateRoot;
    }

    public void setTemplateRoot(String templateRoot) {
        this.templateRoot = templateRoot;
    }

    public Map<String, Object> getNonCoreValues() {
        return nonCoreValues;
    }

    public void setNonCoreValues(Map<String, Object> nonCoreValues) {
        this.nonCoreValues = nonCoreValues;
    }
    
    
}
