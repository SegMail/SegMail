/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.chartjs;

/**
 *
 * @author LeeKiatHaw
 */
public class TestService {
    public static void main(String[] args){
        ChartJSService service = new ChartJSService();
        double[] result = service.outstandingLoanOverTime(0.026, 30, 500000);
        
        for(double pmt : result){
            System.out.println(pmt);
        }
    }
}
