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
public class ChartJSService {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "add")
    public int add(@WebParam(name = "a") int firstNum, @WebParam(name = "b") int secondNum) {
        //TODO write your implementation code here:
        return firstNum + secondNum;
    }
    
    @WebMethod(operationName = "outstandingLoanOverTime")
    public double[] outstandingLoanOverTime(
            @WebParam(name="int") double intRate,
            @WebParam(name="n") int numYears,
            @WebParam(name="p") double principal
            ){
        
        double monthlyInt = intRate/12;
        double onePlusN = 1 + monthlyInt;
        int months = numYears*12;
        //Compute monthly installment first
        double monthlyPmt = principal*monthlyInt*(Math.pow(onePlusN, months)) / (Math.pow(onePlusN, months) - 1);
        
        double[] monthlyAmt = new double[months];
        double outstandingLoan = principal;
        monthlyAmt[0] = outstandingLoan;
        
        for(int i = 1; i < months; i++){
            outstandingLoan = (onePlusN)*outstandingLoan - monthlyPmt;
            monthlyAmt[i] = outstandingLoan;
        }
        
        return monthlyAmt;
    }
    
    
}
