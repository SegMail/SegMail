/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.chartjs;

import eds.entity.program.Program;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ProgramChartJS")
@SessionScoped
public class ProgramChartJS extends Program {
    
    //private final String WEB_SERVICE_ENDPOINT = "/SegMail/ChartJSService";
    private final String WEB_SERVICE_ENDPOINT = "ChartJSService";
    
    private final String WEB_SERVICE_METHOD = "outstandingLoanOverTime";
    
    
    private final String WEB_SERVICE_NAMESPACE_QUALIFIER = "ns2";

    public String getWEB_SERVICE_ENDPOINT() {
        return WEB_SERVICE_ENDPOINT;
    }

    public String getWEB_SERVICE_METHOD() {
        return WEB_SERVICE_METHOD;
    }

    public String getWEB_SERVICE_NAMESPACE_QUALIFIER() {
        return WEB_SERVICE_NAMESPACE_QUALIFIER;
    }
    
    
}
