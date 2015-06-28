/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapAPI;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EVALUATION_PATH")
@TableGenerator(name="EVALUATION_PATH_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
public class EvaluationPath implements Serializable {
    
    private long PATH_ID;
    
    private List<EvaluationPathElement> elements;

    @Id @GeneratedValue(generator="EVALUATION_PATH_SEQ",strategy=GenerationType.TABLE) 
    public long getPATH_ID() {
        return PATH_ID;
    }

    public void setPATH_ID(long PATH_ID) {
        this.PATH_ID = PATH_ID;
    }

    @OneToMany
    public List<EvaluationPathElement> getElements() {
        return elements;
    }

    public void setElements(List<EvaluationPathElement> elements) {
        this.elements = elements;
    }
    
    
}
