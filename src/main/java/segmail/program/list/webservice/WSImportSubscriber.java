/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list.webservice;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSImportSubscriber")
@HandlerChain(file = "handlers-server.xml")
public class WSImportSubscriber {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "setColumns")
    public String setColumns(@WebParam(name = "columns") String columns) {
        return "";
        
    }
    
    /**
     * Returns the last position of the file uploaded and -1 if it is a new file.
     * 
     * @param filename
     * @param fileHash
     * @return 
     */
    @WebMethod(operationName = "checkFileStatus")
    public int checkFileStatus(
            @WebParam(name = "filename") String filename,
            @WebParam(name = "fileHash") String fileHash) {
        
        return -1;
    }
}
