/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import org.joda.time.DateTime;

/**
 *
 * @author KH
 */
public abstract class AuditedObject implements Serializable {
}
