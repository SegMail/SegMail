/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list.webservice;

import eds.component.client.ClientFacade;
import eds.component.data.EntityNotFoundException;
import eds.component.file.FileService;
import eds.entity.file.FileTransaction;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import segmail.component.subscription.SubscriptionService;
import segmail.program.list.ProgramList;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSImportSubscriber")
@HandlerChain(file = "handlers-server.xml")
public class WSImportSubscriber {

    @EJB FileService fileService;
    @EJB SubscriptionService subService;
    
    /**
     * External webservice endpoints cannot inject ClientFacades because the calling 
     * service may not be the actual business user so ClientModule doesn't initialize
     * the correct Client object here. In this case, we are sure that this is an
     * internal service (called on an ERP server where the user is authenticated
     * and accessing a private program) so we are injecting it here.
     */
    @Inject ClientFacade clientFacade;
    
    /**
     * Again not supposed to inject UI beans into webservice endpoints but just for 
     * convenience sake.
     */
    @Inject ProgramList program;
    
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
     * @throws eds.component.data.EntityNotFoundException if listId is not found
     */
    @WebMethod(operationName = "addSubscribers")
    public String addSubscribers(
            @WebParam(name = "subscribers") String subscribers
            //,@WebParam(name = "listId") long listId
            //,@WebParam(name = "clientId") long clientId //Only for internal services we can inject Client object
        ) throws EntityNotFoundException {
        if(program.getListEditingId() <= 0)
            throw new EntityNotFoundException("No list found.");
        
        if(clientFacade.getClient() == null || clientFacade.getClient().getOBJECTID() <= 0)
            throw new EntityNotFoundException("No client found.");
            
            
        JsonReader reader = Json.createReader(new StringReader(subscribers));
        JsonObject subscribersObj = reader.readObject();
        
        int i = 0;
        for(JsonValue subscriber : subscribersObj.values()) {
            if(++i > 1000)
                break;
            System.out.println(subscriber.toString());
            
            JsonObject subObj = (JsonObject)subscriber;
            //System.out.println(subObj.getString("listfield000000007300001"));
        }
        List subscribersList = new ArrayList(subscribersObj.values());
        Map<String, List> results = subService.massSubscribe(clientFacade.getClient().getOBJECTID(), program.getListEditingId(), subscribersList, false);
        
        //Construct the JSON response object
        //Return object will only contain errors
        JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
        for(Map.Entry<String, List> entry : results.entrySet()){
            String key = entry.getKey();
            List values = entry.getValue();
            JsonArrayBuilder errorIndexesBuilder = Json.createArrayBuilder();
            for(Object value : values){
                errorIndexesBuilder.add((JsonValue) value);
            }
            resultObjectBuilder.add(key, errorIndexesBuilder.build());
        }
        String result = resultObjectBuilder.build().toString();
        return result;
    }
}
