/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.chartjs;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(endpointInterface = "seca2.program.chartjs.ChartJSService")
@HandlerChain(file = "/handlers-server.xml")
public class ChartJSServiceImpl implements ChartJSService {

    @Resource
    WebServiceContext wsctx;
    
    /**
     * Web service operation
     * @param firstNum
     * @param secondNum
     * @return 
     */
    @Override
    public int add(int firstNum, int secondNum) {
        //TODO write your implementation code here:
        System.out.println("add method called!");
        
        MessageContext mctx = wsctx.getMessageContext();
        Map http_headers = (Map) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        
        List userList = (List) http_headers.get("Username");
        List passList = (List) http_headers.get("Password");
        
        String username = "";
        String password = "";
        
        if(userList!=null){
        	//get username
        	username = userList.get(0).toString();
        }
        	
        if(passList!=null){
        	//get password
        	password = passList.get(0).toString();
        }
        
        return firstNum + secondNum;
    }
    
    //@WebMethod(operationName = "outstandingLoanOverTime")
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
