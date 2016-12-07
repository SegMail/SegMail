/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.client.ClientFacade;
import eds.component.data.EntityNotFoundException;
import eds.component.file.FileService;
import eds.entity.file.FileTransaction;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import segmail.component.subscription.MassSubscriptionService;
import segmail.component.subscription.SubscriptionContainer;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriptionList;
import segmail.program.list.ProgramList;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSImportSubscribers")
@HandlerChain(file = "handlers-server.xml")
public class WSImportSubscribers {

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
    ProgramSubscribers program;

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
        if (program.getSelectedLists().size() <= 0) {
            throw new EntityNotFoundException("No list found.");
        }
        
        if (clientFacade.getClient() == null || clientFacade.getClient().getOBJECTID() <= 0) {
            throw new EntityNotFoundException("No client found.");
        }
        
        //subContainer.setClient(clientFacade.getClient()); //Do not use ClientFacade or ClientContainer in EJB services because they might not be called in the same context (JSF vs JAX-WS/JAX-RS)
        //subContainer.setList(program.getListEditing());
        subContainer.setListFields(program.getFieldList());
        
        JsonReader reader = Json.createReader(new StringReader(subscribers));
        JsonObject subscribersObj = reader.readObject();

        List<Map<String, Object>> subscribersList = new ArrayList<>();
        //Need to construct the maps ourselves
        for (JsonValue subscriberObj : subscribersObj.values()) {
            
            Map<String, Object> subscriber = this.convertJsonObjectToMap((JsonObject)subscriberObj);
            subscribersList.add(subscriber);
        }

        //Call massSubscribe once per listId
        //This is a workaround!!!
        Map<String, List<Map<String,Object>>> mergedResults = new HashMap<>();
        for(SubscriptionList list : program.getSelectedLists()) {
            //Must set the list before calling massSubscribe
            //In general, this is not a good design because it gets overlooked during coding
            //all params required should be declared in the method
            //subContainer.setList(list); 
            Map<String, List<Map<String,Object>>> results = massSubService.massSubscribe(clientFacade.getClient(),subscribersList,list,false);
            //Combine the results
            for(String key : results.keySet()) {
                if(mergedResults.get(key) == null)
                    mergedResults.put(key, results.get(key));
                else
                    mergedResults.get(key).addAll(results.get(key));
            }
        }

        //Construct the JSON response object from the Map object
        //Return object will only contain errors
        int totalErrors = 0;
        JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
        for (String key : mergedResults.keySet()) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            List<Map<String,Object>> objHavingThisError = mergedResults.get(key);
            for(Map<String,Object> mapObj : objHavingThisError) {
                JsonObjectBuilder jsonObjBuilder = this.convertMapToJson(mapObj);
                arrayBuilder.add(jsonObjBuilder);
                totalErrors++;
            }
            resultObjectBuilder.add(key, arrayBuilder);
        }
        //Add processing stats
        resultObjectBuilder.add("total", subscribersList.size());
        resultObjectBuilder.add("errors", totalErrors );
        
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
