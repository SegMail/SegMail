/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.chartjs;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "ChartJSService")
@Stateless()
public class ChartJSService {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "add")
    public int add(@WebParam(name = "a") int firstNum, @WebParam(name = "b") int secondNum) {
        //TODO write your implementation code here:
        return firstNum + secondNum;
    }
    
    public double[] outstandingLoanOverTime(
            @WebParam(name="int") double intRate,
            @WebParam(name="n") int numYears,
            @WebParam(name="p") double principal
            ){
        
        //Compute monthly installment first
        double monthlyPmt = intRate*principal/(1 - (1/ Math.pow(1+intRate,numYears)));
        
        double[] yearlyAmt = new double[numYears];
        double outstandingLoan = principal;
        yearlyAmt[0] = outstandingLoan;
        
        for(int i = 1; i < numYears; i++){
            outstandingLoan = (1+intRate)*outstandingLoan - monthlyPmt;
            yearlyAmt[i] = outstandingLoan;
        }
        
        return yearlyAmt;
>>>>>>> origin/master
    }
}
