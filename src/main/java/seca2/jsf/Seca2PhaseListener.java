/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author KH
 */
public class Seca2PhaseListener implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent event) {
        System.out.println("After phase("+event.getPhaseId()+")");
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        System.out.println("Before phase("+event.getPhaseId()+")");
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
    
}
