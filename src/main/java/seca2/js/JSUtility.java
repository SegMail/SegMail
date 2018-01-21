/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.js;

import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 *
 * @author LeeKiatHaw
 */
public class JSUtility {
    
    public static Map<String, Object> convertJsonObjectToMap(JsonObject jsonObj) {

        Map<String, Object> mapObj = new HashMap<>();

        for (String key : jsonObj.keySet()) {
            JsonValue value = (JsonValue) jsonObj.get(key);
            //subscriber.put(key, value);
            JsonValue.ValueType vType = value.getValueType();
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
    
    public static JsonObjectBuilder convertMapToJson(Map<String, Object> mapObj) {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        for(String key : mapObj.keySet()) {
            if(key == null){
                continue;
            }
            jsonObjBuilder.add(key, mapObj.get(key).toString()); //Only strings are allowed here
        }
        return jsonObjBuilder;
    }
}
