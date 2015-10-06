/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

/**
 *
 * @author LeeKiatHaw
 */
public class EmailTemplateFactory {
    
    public static enum TYPE{
        CONFIRMATION,
        NEWSLETTER,
        AUTORESPONDER
    }
    
    public static EmailTemplate getEmailTemplateInstance(TYPE type){
        switch(type){
            case CONFIRMATION   :   return new ConfirmationEmailTemplate();
            case NEWSLETTER     :   return new NewsletterEmailTemplate();
            default             :   return null;
        }
    }
    
    public static Class<? extends EmailTemplate> getEmailTemplateClass(TYPE type){
        switch(type){
            case CONFIRMATION   :   return ConfirmationEmailTemplate.class;
            case NEWSLETTER     :   return NewsletterEmailTemplate.class;
            default             :   throw new RuntimeException("EmailTemplateFactory cannot identify the type "+type.toString());
        }
    }
}
