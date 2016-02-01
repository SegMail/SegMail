/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email.mailmerge;

import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * You only need to define the COMMAND
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="GLOBAL_MERGE_LABEL")
public class GlobalMergeLabel extends MergeLabel{

    private String COMMAND;

    public String getCOMMAND() {
        return COMMAND;
    }

    public void setCOMMAND(String COMMAND) {
        this.COMMAND = COMMAND;
    }
    
    public void setCOMMAND(MERGE_COMMAND COMMAND) {
        this.COMMAND = COMMAND.toString();
    }

    @Override
    protected String generateValue(Map<String, Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
