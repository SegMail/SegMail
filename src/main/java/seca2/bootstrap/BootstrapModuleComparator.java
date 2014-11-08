/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.util.Comparator;

/**
 *
 * @author LeeKiatHaw
 */
public class BootstrapModuleComparator implements Comparator<BootstrapModule> {

    @Override
    public int compare(BootstrapModule o1, BootstrapModule o2) {
        int w1 = o1.executionSequence();
        int w2 = o2.executionSequence();
        
        return w1 - w2;
    }
    
}
