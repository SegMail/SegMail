/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.user;

/**
 *
 * @author LeeKiatHaw
 */
public enum PWD_PROCESSING_STATUS {
    NEW("NEW"),
    PROCESSED("PROCESSED");
    
    public final String label;
    
    private PWD_PROCESSING_STATUS(String label){
        this.label = label;
    }
}
