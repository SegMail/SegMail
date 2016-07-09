/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list.webservice;

import eds.component.file.FileService;
import eds.entity.file.FileTransaction;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
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

    @EJB FileService fileService;
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
        
        FileTransaction file = fileService.createOrGetFileTransaction(filename, fileHash,"");
        
        return file.getLAST_PROCESSING_POSITION();
    }

    /**
     * Web service operation
     * Tries to insert all subscribers provided and returns the following:
     * 1) Total subscriptions created
     * 2) Number of subscribers who are already subscribed to other lists of the
     * client
     * 3) 
     * @param subscribers
     * @param listId
     * @return
     */
    @WebMethod(operationName = "addSubscribers")
    public String addSubscribers(
            @WebParam(name = "subscribers") String subscribers,
            @WebParam(name = "listId") long listId) {
        
        
        JsonReader reader = Json.createReader(new StringReader(subscribers));
        JsonObject subscribersObj = reader.readObject();
        
        for(JsonValue subscriber : subscribersObj.values()) {
            
        }
        
        return null;
    }
}
