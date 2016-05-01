/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditJob")
public class FormEditJob {
    @Inject ProgramBatch program;
}
