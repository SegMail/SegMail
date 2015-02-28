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
import javax.jws.WebResult;

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
    
    @WebMethod(operationName = "outstandingLoanOverTime",action="outstandingLoanOverTime")
    //@WebResult
    public double[] outstandingLoanOverTime(
            @WebParam(name="int")/*, targetNamespace="ns2")*/ double intR,
            @WebParam(name="n")/*, targetNamespace="ns2")*/ int n,
            @WebParam(name="p")/*, targetNamespace="ns2")*/ double p
            ){
        //if any of the parameters are empty, do not do any calculation and return null;
        //Is it ok to do this or throw exception?
        //if(intRate <= 0) return null;
        //if(numYears <= 0) return null;
        //if(principal <= 0) return null;
        
        double monthlyInt = intR/12;
        double onePlusN = 1 + monthlyInt;
        int months = n*12;
        //Compute monthly installment first
        double monthlyPmt = p*monthlyInt*(Math.pow(onePlusN, months)) / (Math.pow(onePlusN, months) - 1);
        
        double[] monthlyAmt = new double[months];
        double outstandingLoan = p;
        monthlyAmt[0] = outstandingLoan;
        
        for(int i = 1; i < months; i++){
            outstandingLoan = (onePlusN)*outstandingLoan - monthlyPmt;
            monthlyAmt[i] = outstandingLoan;
        }

        return monthlyAmt;

    }
}
