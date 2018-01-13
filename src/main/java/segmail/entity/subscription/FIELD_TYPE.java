/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
public enum FIELD_TYPE {
    TEXT("TEXT"),
    DATE("DATE"),
    EMAIL("EMAIL");
    
    public final String name;
    
    private FIELD_TYPE(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    public void validate(Object value) throws SubscriberFieldValidationException {
        if(value == null)
            throw new SubscriberFieldValidationException("Field values cannot be null.");
        
        switch(this) {
            case EMAIL : validateEMAIL(value.toString());
                break;
            case DATE : validateDATE(value);
                break;
            case TEXT : validateTEXT(value.toString());
                break;
            default : break;
        }
    }
    
    void validateEMAIL(String email) throws SubscriberFieldValidationException {
        if(!EmailValidator.getInstance().isValid(email))
            throw new SubscriberFieldValidationException("Email "+email+" is not valid.");
    }
    
    void validateDATE(Object date) throws SubscriberFieldValidationException {
        if(!date.getClass().isAssignableFrom(java.sql.Date.class)
                || !date.getClass().isAssignableFrom(DateTime.class)
                || !date.getClass().isAssignableFrom(String.class))
            throw new SubscriberFieldValidationException("Date object "+date.toString()+" is not valid.");
        
        if(date.getClass().isAssignableFrom(String.class)) {
            try {
                String dateValue = (String) date;
                DateTime dt = DateTime.parse(dateValue);
            } catch (Exception ex) { // Any parsing errors
                throw new SubscriberFieldValidationException("Invalid Date object: "+ex.getMessage());
            }
        }
    }
    
    void validateTEXT(String value) {
        
    }
}
