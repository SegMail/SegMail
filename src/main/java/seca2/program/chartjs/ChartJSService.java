/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.chartjs;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/**
 *
 * @author LeeKiatHaw
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface ChartJSService {
    
    @WebMethod(operationName = "add")
    public int add(@WebParam(name = "a") int firstNum, @WebParam(name = "b") int secondNum);
}
