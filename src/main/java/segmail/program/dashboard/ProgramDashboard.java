/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.dashboard;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import seca2.program.Program;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ProgramDashboard")
@SessionScoped
public class ProgramDashboard extends Program {
    
    private final int DEFAULT_DAYS = 30;
    private final int[] DAYS = { 7, 14, 30, 90 };
    
    private final int MAX_LATEST_SUBSCRIBERS = 5;
    
    private boolean showWelcome = false;
    
    private boolean dontShowThisAgain;
    
    private boolean redirectToWizard = false;

    public boolean isRedirectToWizard() {
        return redirectToWizard;
    }

    public void setRedirectToWizard(boolean redirectToWizard) {
        this.redirectToWizard = redirectToWizard;
    }

    public boolean isDontShowThisAgain() {
        return dontShowThisAgain;
    }

    public void setDontShowThisAgain(boolean dontShowThisAgain) {
        this.dontShowThisAgain = dontShowThisAgain;
    }

    public boolean isShowWelcome() {
        return showWelcome;
    }

    public void setShowWelcome(boolean showWelcome) {
        this.showWelcome = showWelcome;
    }

    public int getMAX_LATEST_SUBSCRIBERS() {
        return MAX_LATEST_SUBSCRIBERS;
    }

    public int getDEFAULT_DAYS() {
        return DEFAULT_DAYS;
    }

    public int[] getDAYS() {
        return DAYS;
    }

    @Override
    public void clearVariables() {
        
    }

    @Override
    public void initRequestParams() {
        
    }

    @Override
    public void initProgram() {
        
    }
    
}
