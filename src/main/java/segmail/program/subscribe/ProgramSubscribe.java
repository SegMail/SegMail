/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe;

import java.util.List;
import java.util.Map;
import seca2.program.Program;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramSubscribe extends Program {

    @Override
    public void initProgramParams() {
        Map<String,String[]> namedParams = this.reqContainer.getPogramParamsNamed();
        List<String> orderedParams = this.reqContainer.getProgramParamsOrdered();
        
        //Assume that param 1 is the program command
        String command = (orderedParams != null && !orderedParams.isEmpty()) ? orderedParams.get(0).toString().toUpperCase() : "";
        
        if(command.equals("CONFIRM")) {
            confirmSubscription();
            return;
        }
        
        if(command.equals("UNSUBSCRIBE")) {
            unsubscribe();
            return;
        }
        
    }
    
    public void confirmSubscription() {
        
    }
    
    public void unsubscribe() {
        
    }
}
