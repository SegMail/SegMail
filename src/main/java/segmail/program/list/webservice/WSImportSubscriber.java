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
import java.util.HashMap;
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
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParser;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.jboss.logging.Logger;
import segmail.component.subscription.MassSubscriptionService;
import segmail.component.subscription.SubscriptionContainer;
import segmail.component.subscription.SubscriptionService;
import segmail.program.list.ProgramList;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSImportSubscriber")
@HandlerChain(file = "handlers-server.xml")
public class WSImportSubscriber {

    @EJB
    FileService fileService;
    @EJB
    SubscriptionService subService;
    @EJB
    MassSubscriptionService massSubService;

    /**
     * External webservice endpoints cannot inject ClientFacades because the
     * calling service may not be the actual business user so ClientModule
     * doesn't initialize the correct Client object here. In this case, we are
     * sure that this is an internal service (called on an ERP server where the
     * user is authenticated and accessing a private program) so we are
     * injecting it here.
     */
    @Inject
    ClientFacade clientFacade;
    
    @Inject
    SubscriptionContainer subContainer;

    /**
     * Again not supposed to inject UI beans into webservice endpoints but just
     * for convenience sake.
     */
    @Inject
    ProgramList program;

    /**
     * Returns the last position of the file uploaded and -1 if it is a new
     * file.
     *
     * @param filename
     * @param fileHash
     * @return
     */
    @WebMethod(operationName = "checkFileStatus")
    public int checkFileStatus(
            @WebParam(name = "filename") String filename,
            @WebParam(name = "fileHash") String fileHash) {

        FileTransaction file = fileService.createOrGetFileTransaction(filename, fileHash, "");

        return file.getLAST_PROCESSING_POSITION();
    }

    /**
     * Web service operation Tries to insert all subscribers provided and
     * returns the following: 1) Total subscriptions created 2) Number of
     * subscribers who are already subscribed to other lists of the client 3)
     *
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
        if (program.getListEditingId() <= 0) {
            throw new EntityNotFoundException("No list found.");
        }
        subContainer.setList(program.getListEditing());
        subContainer.setListFields(program.getFieldList());

        if (clientFacade.getClient() == null || clientFacade.getClient().getOBJECTID() <= 0) {
            throw new EntityNotFoundException("No client found.");
        }

        JsonReader reader = Json.createReader(new StringReader(subscribers));
        JsonObject subscribersObj = reader.readObject();

        List<Map<String, Object>> subscribersList = new ArrayList<>();
        //Need to construct the maps ourselves
        for (JsonValue subscriberObj : subscribersObj.values()) {
            
            Map<String, Object> subscriber = this.convertJsonObjectToMap((JsonObject)subscriberObj);
            subscribersList.add(subscriber);
        }

        Map<String, List<Map<String,Object>>> results = new HashMap<>();
        try {
            results = massSubService.massSubscribe(subscribersList, false);
        } catch(Exception ex) {
            ex.printStackTrace(System.out);
            //System.out.println("First subscriber: "+subscribersList.get(0).toString());
            //System.out.println("Last subscriber: "+subscribersList.get(subscribersList.size()-1).toString());
        }
        

        //Construct the JSON response object from the Map object
        //Return object will only contain errors
        JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
        for (String key : results.keySet()) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            List<Map<String,Object>> objHavingThisError = results.get(key);
            for(Map<String,Object> mapObj : objHavingThisError) {
                JsonObjectBuilder jsonObjBuilder = this.convertMapToJson(mapObj);
                arrayBuilder.add(jsonObjBuilder);
            }
            resultObjectBuilder.add(key, arrayBuilder);
        }
        String result = resultObjectBuilder.build().toString();
        return result;
    }

    protected Map<String, Object> convertJsonObjectToMap(JsonObject jsonObj) {

        Map<String, Object> mapObj = new HashMap<>();

        for (String key : jsonObj.keySet()) {
            JsonValue value = (JsonValue) jsonObj.get(key);
            //subscriber.put(key, value);
            ValueType vType = value.getValueType();
            //Convert everything to String
            switch (vType) {
                case NUMBER:
                    mapObj.put(key, Integer.toString(jsonObj.getJsonNumber(key).intValueExact()));
                    break;
                case STRING:
                    mapObj.put(key, jsonObj.getJsonString(key).getString());
                    break;
                case TRUE:
                case FALSE:
                    mapObj.put(key, Boolean.toString(jsonObj.getBoolean(key)));
                    break;
                default: //subscriber.put(key, subObj.getJsonString(key)); break;
                    break; //If the type is not recognized, don't put it in.
            }
        }

        return mapObj;
    }

    protected JsonObjectBuilder convertMapToJson(Map<String, Object> mapObj) {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        for(String key : mapObj.keySet()) {
            if(key == null){
                System.out.println("key: "+key);
                continue;
            }
            jsonObjBuilder.add(key, mapObj.get(key).toString()); //Only strings are allowed here
        }
        return jsonObjBuilder;
    }
}
