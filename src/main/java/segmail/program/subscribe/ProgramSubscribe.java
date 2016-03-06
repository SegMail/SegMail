/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe;

import eds.entity.program.Program;
import javax.ejb.EJB;
import segmail.component.landing.LandingService;

/**
 * Offline program to process subscriptions at landing servers.
 * 
 * @author LeeKiatHaw
 */
public class ProgramSubscribe extends Program {
    
    @EJB private LandingService landingService;
    
    
}
