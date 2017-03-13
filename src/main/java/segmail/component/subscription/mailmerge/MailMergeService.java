/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.mailmerge;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import eds.component.transaction.TransactionService;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import segmail.component.subscription.SubscriptionService;
import seca2.entity.landing.ServerInstance;
import segmail.component.subscription.ListService;
import segmail.entity.campaign.Campaign;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_STATUS;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;
import segmail.entity.subscription.email.mailmerge.MailMergeRequest;

/**
 * A MailMerge is a piece of information that comes from an EnterpriseObject in
 * the system and it is represented by a code that can be inserted in any
 * content.
 *
 *
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailMergeService {

    @EJB
    private GenericObjectService objectService;
    @EJB
    private UpdateObjectService updateService;
    @EJB
    private TransactionService transService;

    @EJB
    private SubscriptionService subscriptionService;
    @EJB
    private LandingService landingService;
    @EJB
    private ListService listService;
    
    /**
     *
     * @param content
     * @param listId
     * @param subscribers
     * @return
     */
    
    public String parseMultipleContent(String content, long listId, List<SubscriberAccount> subscribers) {

        return "";
    }

    /**
     * As of now, we only recognize 3 types of mailmerge labels: 1) Confirmation
     * link 2) Subscriber attributes 3) Unsubscribe link
     *
     * I can't think of any good way to create a logical generator that can be
     * configurable in the frontend. So we will just hard code everything first.
     * <br>
     * Generates the body content with the proper confirmation link, which has
     * an expiry date.
     *
     * @param text
     * @param confirmationKey
     * @param email
     * @return
     * @throws eds.component.data.IncompleteDataException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String parseConfirmationLink(
            String text, //Don't pass in the AutoConfirmEmail class because that was a huge mistake and we might want to correct it in the future
            //String landingServerAddress, 
            //String email,
            //long listId
            String confirmationKey) throws IncompleteDataException {
        //!!! do this only if there is a link to generate!
        if (text == null || text.isEmpty() || !text.contains(MAILMERGE_REQUEST.CONFIRM.label())) {
            return text;
        }

        //1. Create a transaction with expiry date 
        MailMergeRequest trans = transService.getTransactionByKey(confirmationKey, MailMergeRequest.class);
        if (trans == null) {
            trans = new MailMergeRequest();
            //trans.setPROGRAM(MAILMERGE_REQUEST.CONFIRM.name().toLowerCase());
            trans.setPROGRAM(MAILMERGE_REQUEST.CONFIRM.program);
            trans.overrideSTATUS(MAILMERGE_STATUS.UNPROCESSED);
            trans.setMAILMERGE_LABEL(MAILMERGE_REQUEST.CONFIRM.name);
            trans.setTRANSACTION_KEY(confirmationKey); //More like an override
            updateService.getEm().persist(trans);
        } else {
            trans.overrideSTATUS(MAILMERGE_STATUS.UNPROCESSED);
            updateService.getEm().merge(trans);
        }
        // Get the subscription's confirmation key

        //2. Create the transaction parameters
        /*EnterpriseTransactionParam subscriberParam = new EnterpriseTransactionParam();
         subscriberParam.setOWNER(trans);
         subscriberParam.setPARAM_KEY(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME);
         subscriberParam.setPARAM_VALUE(email);
         updateService.getEm().persist(subscriberParam);

         EnterpriseTransactionParam listParam = new EnterpriseTransactionParam();
         listParam.setOWNER(trans);
         listParam.setPARAM_KEY(SubscriptionService.DEFAULT_KEY_FOR_LIST);
         listParam.setPARAM_VALUE(Long.toString(listId));
         updateService.getEm().persist(listParam);
         //Might want to use guid instead.
         */
        //3. Return the link with program name "confirm" and the generated transaction ID 
        ServerInstance landingServer
                = landingService.getNextServerInstance(
                        LandingServerGenerationStrategy.ROUND_ROBIN,
                        ServerNodeType.WEB);
        if (landingServer == null) {
            throw new IncompleteDataException("Please contact app administrator to set a landing server.");
        }

        String confirmLink = landingServer.getURI().concat("/").concat(trans.getPROGRAM()).concat("/").concat(trans.getTRANSACTION_KEY());
        String confirmLinkHtml = "<a target='_blank' href='"+confirmLink+"'>"+MAILMERGE_REQUEST.CONFIRM.defaultHtmlText()+"</a>";
        
        String newEmailBody = text.replace(MAILMERGE_REQUEST.CONFIRM.label(), confirmLinkHtml);

        return newEmailBody;
    }

    /**
     * In the future, we should build a MailmergeTag EnterpriseObject class that 
     * can store the attribute and object key of its owner object.
     * 
     * This is used by campaigns.
     * 
     * @param text
     * @param fieldValueMap //MailmergeTag => Field value
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String parseSubscriberTags(String text, Map<String,String> fieldValueMap) {
        if(text == null || text.isEmpty())
            return text;
        
        for(String mmTag : fieldValueMap.keySet()){
            text = text.replace(mmTag, fieldValueMap.get(mmTag));
        }
        
        return text;
    }
    
    /**
     * 
     * @param subscriberIds
     * @param fields
     * @param values
     * @return subscriberId => {mailmerge-tag} => SubscriberFieldValue
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Map<Long,Map<String,String>> createMMValueMap(List<Long> subscriberIds, List<SubscriptionListField> fields, List<SubscriberFieldValue> values) {
        Map<Long,Map<String,String>> results = new HashMap<>();
        
        for(SubscriptionListField field : fields) {
            String key = (String) field.generateKey();
            String tag = field.getMAILMERGE_TAG();
            
            for(Long subscriberId : subscriberIds) {
                if(!results.containsKey(subscriberId))
                    results.put(subscriberId, new HashMap<String,String>());

                Map<String,String> subscriberMap = results.get(subscriberId);
                
                //If the subscriber has the field value, insert into map.
                //Else, just insert an empty field
                subscriberMap.put(tag, "");
                for(SubscriberFieldValue value : values) {
                    if(value.getFIELD_KEY() != null && value.getFIELD_KEY().equals(key)) {
                        subscriberMap.put(tag, value.getVALUE());
                        break;
                    }
                }
            }
        }
        return results;
    }
    
    /**
     * For sending confirmation emails, where the listId is known.
     * Used by confirmation and welcome emails.
     * 
     * @param text
     * @param subscriberId
     * @param listId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String parseForAutoresponders(String text, SubscriberAccount subscriber , SubscriptionList list) {
        if(text == null || text.isEmpty())
            return "";
        
        List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(list.getOBJECTID());
        List<Long> ids = new ArrayList<>();
        ids.add(subscriber.getOBJECTID());
        List<SubscriberFieldValue> values = subscriptionService.getSubscriberValuesBySubscriberIds(ids);
        Map<Long,Map<String,String>> map = this.createMMValueMap(ids, fields, values);
        
        String result = text;
            
        result = parseSubscriberTags(result, map.get(subscriber.getOBJECTID()));
        result = parseStandardListTags(result, list);
        result = parseExtraSubscriberTags(result, values.get(0).getOWNER(), DateTime.now());
        
        return result;
    }

    /**
     * - Check if the transaction key was already created by looking at the
     * params. - If exist, re-use the same transaction key - For unsubscription
     * key, we use a more consistent one: [email]+[salt]+[listId]+[salt] so that
     * we can check if it exists before - Let's use the subscriber's ID (the one
     * assigned to the client) - Don't store another transaction because this is
     * not a time-limited transaction. [no more]
     *
     *
     * @param text
     * @param unsubscribeKey
     * @return
     * @throws eds.component.data.IncompleteDataException if landing/WEB server is not set
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String parseUnsubscribeLink(String text, String unsubscribeKey) throws IncompleteDataException {

        if (text == null || text.isEmpty() || !text.contains(MAILMERGE_REQUEST.UNSUBSCRIBE.label())) {
            return text;
        }
        
        // Check if key exists
        /*MailMergeRequest trans = transService.getTransactionByKey(unsubscribeKey, MailMergeRequest.class);
         if(trans == null) {
         trans = new MailMergeRequest();
         trans.setPROGRAM(MAILMERGE_REQUEST.UNSUBSCRIBE.name().toLowerCase());
         trans.overrideSTATUS(MAILMERGE_STATUS.UNPROCESSED);
         trans.setTRANSACTION_KEY(unsubscribeKey); //More like an override
         updateService.getEm().persist(trans);
         }*/
        /**/ServerInstance landingServer
                = landingService.getNextServerInstance(
                        LandingServerGenerationStrategy.ROUND_ROBIN,
                        ServerNodeType.WEB);
        if (landingServer == null) {
            throw new IncompleteDataException("Please contact app administrator to set a landing server.");
        }

        String unsubLink = landingServer.getURI().concat("/").concat(MAILMERGE_REQUEST.UNSUBSCRIBE.program).concat("/").concat(unsubscribeKey);
        String unsubLinkHtml = "<a target='_blank' href='"+unsubLink+"'>"+MAILMERGE_REQUEST.UNSUBSCRIBE.defaultHtmlText()+"</a>";
        
        return text.replace(MAILMERGE_REQUEST.UNSUBSCRIBE.label(), unsubLinkHtml);
    }

    public String parseEverything(String text, Map<String, Object> params) throws IncompleteDataException {
        String result = text;
        //Has to be a better way to register all these parsing in a queue and process them together
        result = this.parseConfirmationLink(result, (String) params.get(MAILMERGE_REQUEST.CONFIRM.label()));
        this.parseUnsubscribeLink(result, (String) params.get(MAILMERGE_REQUEST.UNSUBSCRIBE.label()));
        //result = this.parseListAttributes(result, (Long) params.get("LISTID"));
        //result = this.parseMultipleContent(result, (Long) params.get("LISTID"));

        return result;
    }
    
    /**
     * Gets a test link for the mailmerge label
     * 
     * @param label
     * @return 
     */
    public String getSystemTestLink(String label) throws DataValidationException, IncompleteDataException{
        MAILMERGE_REQUEST request = MAILMERGE_REQUEST.getByLabel(label);
        if(request == null)
            throw new DataValidationException("Invalid label");
        
        ServerInstance testServer = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.WEB);
        if(testServer == null || testServer.getURI() == null || testServer.getURI().isEmpty())
            throw new IncompleteDataException("Test server is not setup properly. Please contact your system admin.");
        
        String testServerAddress = testServer.getURI();
        if(!testServerAddress.endsWith("/"))
            testServerAddress = testServerAddress + "/";
        
        String name = request.program();
        String testLink = testServerAddress + name + "/test";
        
        return testLink;
    }
    
    public String parseStandardListTags(String text, SubscriptionList list) {
        text = text.replace(SubscriptionList.MM_SENDER_NAME, list.getSEND_AS_NAME());
        text = text.replace(SubscriptionList.MM_SUPPORT_EMAIL, list.getSUPPORT_EMAIL());
        
        return text;
    }
    
    public String parseStandardCampaignTags(String text, Campaign campaign) {
        text = text.replace(SubscriptionList.MM_SENDER_NAME, campaign.getOVERRIDE_SEND_AS_NAME());
        text = text.replace(SubscriptionList.MM_SUPPORT_EMAIL, campaign.getOVERRIDE_SUPPORT_EMAIL());
        
        return text;
    }
    
    public String parseExtraSubscriberTags(String text, SubscriberAccount subscriber, DateTime now) {
        text = text.replace(SubscriberAccount.MM_DATE_OF_SUBSCRIPTION, DateFormat.getDateInstance().format(subscriber.getDATE_CREATED()));
        Period length = (new Period(
                new DateTime(subscriber.getDATE_CREATED().getTime()),
                now
        ));
        length = length.withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
        text = text.replace(SubscriberAccount.MM_LENGTH_OF_SUBSCRIPTION, length.toString(
                new PeriodFormatterBuilder()
                        .printZeroAlways()
                        .printZeroRarelyLast()
                .appendYears().appendSuffix(" year"," years")
                .appendSeparator(" ")
                .appendMonths().appendSuffix(" month"," months")
                .appendSeparator(" ")
                .appendDays().appendSuffix(" day"," days")
                .toFormatter()
        ));
        
        return text;
    }
}
