/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.client.ClientFacade;
import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipNotFoundException;
import eds.component.mail.InvalidEmailException;
import eds.component.mail.MailServiceOutbound;
import eds.entity.client.Client;
import eds.entity.mail.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.TransactionSynchronizationRegistry;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_Client;
import segmail.entity.campaign.Assign_Campaign_List;
import segmail.entity.campaign.Assign_Campaign_List_;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.link.CampaignActivityOutboundLink;
import segmail.entity.campaign.link.CampaignActivityOutboundLink_;
import segmail.entity.campaign.link.CampaignLinkClick;
import segmail.entity.campaign.Trigger_Email_Activity;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.Subscription_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class CampaignExecutionService {

    public final int BATCH_SIZE = 1000;
    
    @Inject ClientFacade clientFacade;

    @EJB
    GenericObjectService objService;
    @EJB
    UpdateObjectService updService;
    @EJB
    CampaignService campService;
    @EJB
    MailMergeService mmService;
    @EJB
    SubscriptionService subService;
    @EJB
    ListService listService;
    @EJB
    MailServiceOutbound mailService;
    @EJB
    CampaignExecutionHelperService helper; 
    
    @Resource
    TransactionSynchronizationRegistry txReg;

    /**
     * Executes the campaign activity from the [start]th subscriber to
     * [start]+size th subscriber.
     * 
     * Designed to be processed like a background job, but in actual fact no 
     * service methods should be designed just for foreground/background processing.
     * Hence it must be called in a new transaction context. However, since we 
     * will keep getting transaction timeout error, we should keep this method 
     * non-transactional but call helper methods which have new transaction scope.
     * 
     *
     * @param campaignActivityId
     * @param maxSize
     * @throws eds.component.data.EntityNotFoundException
     * @throws eds.component.data.RelationshipNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeCampaignActivity(long campaignActivityId, final int maxSize)
            throws EntityNotFoundException, RelationshipNotFoundException, DataValidationException, IncompleteDataException, InvalidEmailException {
        
        CampaignActivity campaignActivity = campService.getCampaignActivity(campaignActivityId); //DB hit
        if (campaignActivity == null) {
            throw new EntityNotFoundException(CampaignActivity.class, campaignActivityId);
        }
        
        List<Campaign> campaigns = objService.getAllSourceObjectsFromTarget(campaignActivityId, Assign_Campaign_Activity.class, Campaign.class); //DB hit
        if (campaigns == null || campaigns.isEmpty())
            throw new RelationshipNotFoundException("CampaignActivity "+campaignActivityId+" is not assigned to any Campaign.");
        Campaign campaign = campaigns.get(0);
        
        List<SubscriptionList> targetLists = objService.getAllTargetObjectsFromSource(campaign.getOBJECTID(), Assign_Campaign_List.class, SubscriptionList.class); //DB hit
        if (targetLists == null || targetLists.isEmpty())
            throw new RelationshipNotFoundException("Campaign "+campaign.getOBJECTID()+" is not assigned any target lists.");
        
        List<SubscriptionListField> targetListFields = listService.getFieldsForLists(targetLists);//DB hit
        
        List<Client> clientLists = objService.getAllTargetObjectsFromSource(campaign.getOBJECTID(), Assign_Campaign_Client.class, Client.class);//DB hit
        if (clientLists == null || clientLists.isEmpty())
            throw new RelationshipNotFoundException("Campaign "+campaign.getOBJECTID()+" is not assigned to any Clients.");
        
        int count = 0; 
        int increment = 1;
        final int maxCount = (maxSize <= 0) ? Integer.MAX_VALUE : maxSize;
        final int batch_size = Math.min(BATCH_SIZE, maxCount);
        /**
         * Loop until either
         * 1) number of records processed has reached maxCount or
         * 2) the end of the list has been reached ie. no more records are being processed
         * in the next call
         */
        int i = 0; //For subscribers
        while (count < maxCount && increment > 0) {
            List<SubscriberAccount> subscribers = 
                    helper.getUnsentSubscriberEmailsForCampaign(
                            campaign.getOBJECTID(), campaignActivityId, i++, batch_size); //DB hit
            
            increment = this.sendEmails(
                    campaign, campaignActivity,
                    subscribers, clientLists, targetListFields);
            count += increment;
        }
        
        helper.updateActivityStatus(campaignActivity);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getSubscriberEmailsForCampaign(long campaignId, int startIndex, int size) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<SubscriberAccount> fromSubAcc = query.from(SubscriberAccount.class);
        Root<Subscription> fromSubp = query.from(Subscription.class);
        Root<Assign_Campaign_List> fromAssignCampList = query.from(Assign_Campaign_List.class);

        query.select(fromSubAcc.get(SubscriberAccount_.EMAIL));
        query.where(
                builder.and(
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.SOURCE), campaignId),
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.TARGET), fromSubp.get(Subscription_.TARGET)),
                        builder.equal(fromSubp.get(Subscription_.TARGET), fromSubAcc.get(SubscriberAccount_.OBJECTID))
                )
        );

        List<String> results = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(size)
                .getResultList();

        return results;

    }

    /**
     * A long query but worth the performance. If a subscriber appears in more
     * than 1 list, only 1 email will be sent. 1 Email per CampaignActivity per
     * SubscriberAccount. Only send to Subscriptions with CONFIRMED status.
     *
     * @param campaignActivityId
     * @param startIndex
     * @param size
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SubscriberAccount> getUnsentSubscriberEmailsForCampaign(long campaignId, long campaignActivityId, int startIndex, int size) {
        return helper.getUnsentSubscriberEmailsForCampaign(campaignId, campaignActivityId, startIndex, size);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean continueCampaignActivity(long campaignActivityId) {
        long targeted = campService.countTargetedSubscribersForActivity(campaignActivityId);
        long sent = campService.countEmailsSentForActivity(campaignActivityId);
        
        return sent <= targeted;
    }
    
    /**
     * By using a map, this actually guarantees the uniqueness of the results. 
     * 1 subcriber to 1 code. If the subscriber is subscribed to multiple lists,
     * only the last subscription will be returned. If they click on any links 
     * with any of their own unsubscribe codes, they will be able to see all the 
     * lists that they are subscribed to and select which ones they want to unsubscribe
     * from (lists from the same client of course). 
     * 
     * @param subscribers
     * @param clientId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Map<SubscriberAccount,String> getUnsubscribeCodes(List<SubscriberAccount> subscribers, long clientId) {
        return helper.getUnsubscribeCodes(subscribers, clientId);
    }
    
    /**
     * If 
     * - the Campaign is not in executing state, 
     * - or emailKey is null or empty (link does not have a source, meaning it came from a test email),
     * 
     * do not create new clicks.
     * 
     * @param linkKey unique key of the link
     * @param emailKey source of the link
     * @return
     * @throws EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String getRedirectLinkAndUpdateHit(String linkKey, String emailKey) throws EntityNotFoundException {
        CampaignActivityOutboundLink link = getLinkByKey(linkKey);
        if(link == null)
            throw new EntityNotFoundException("Link key "+linkKey+" not found.");
        
        if(!ACTIVITY_STATUS.NEW.name.equals(link.getOWNER().getSTATUS()) && 
                !ACTIVITY_STATUS.EDITING.name.equals(link.getOWNER().getSTATUS()) &&
                emailKey != null && !emailKey.isEmpty()
                ) {
            CampaignLinkClick newLinkClick = new CampaignLinkClick();
            newLinkClick.setLINK_KEY(linkKey);
            newLinkClick.setSOURCE_KEY(emailKey);

            updService.persist(newLinkClick);
        }
        
        return link.getLINK_TARGET();
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public CampaignActivityOutboundLink getLinkByKey(String linkKey) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<CampaignActivityOutboundLink> query = builder.createQuery(CampaignActivityOutboundLink.class);
        Root<CampaignActivityOutboundLink> fromLink = query.from(CampaignActivityOutboundLink.class);
        
        query.select(fromLink);
        query.where(builder.equal(fromLink.get(CampaignActivityOutboundLink_.LINK_KEY),linkKey));
        
        List<CampaignActivityOutboundLink> results = objService.getEm().createQuery(query)
                .getResultList();
        
        if(results == null || results.isEmpty())
            return null;
        
        return results.get(0);
    }
    
    public void sendPreview(CampaignActivity emailActivity, List<String> previewEmails, long clientId) throws DataValidationException, IncompleteDataException, InvalidEmailException, RelationshipNotFoundException {
        
        List<Campaign> campaigns = objService.getAllSourceObjectsFromTarget(emailActivity.getOBJECTID(), Assign_Campaign_Activity.class, Campaign.class); //DB hit
        if (campaigns == null || campaigns.isEmpty())
            throw new RelationshipNotFoundException("CampaignActivity "+emailActivity.getOBJECTID()+" is not assigned to any Campaign.");
        Campaign campaign = campaigns.get(0);
        
        List<SubscriptionList> targetLists = objService.getAllTargetObjectsFromSource(campaign.getOBJECTID(), Assign_Campaign_List.class, SubscriptionList.class); //DB hit
        if (targetLists == null || targetLists.isEmpty())
            throw new RelationshipNotFoundException("Campaign "+campaign.getOBJECTID()+" is not assigned any target lists.");
        
        List<SubscriptionListField> targetListFields = listService.getFieldsForLists(targetLists);//DB hit
        
        for(String email : previewEmails) {
            Email preview = new Email();
            //Set the header info of the email
            preview.setSUBJECT(emailActivity.getACTIVITY_NAME());
            preview.addRecipient(email);
            preview.setSENDER_ADDRESS(campaign.getOVERRIDE_SEND_AS_EMAIL());
            preview.setSENDER_NAME(campaign.getOVERRIDE_SEND_AS_NAME());

            //Set the body of the email
            String content = emailActivity.getACTIVITY_CONTENT_PROCESSED();

            //String testUnsubLink = mmService.getSystemTestLink(MAILMERGE_REQUEST.UNSUBSCRIBE.label());
            //content = content.replace(MAILMERGE_REQUEST.UNSUBSCRIBE.label(), testUnsubLink);
            mmService.parseUnsubscribeLink(content,"test");
            preview.setBODY(content);

            mailService.queueEmail(preview, DateTime.now());
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int sendEmails(
            Campaign campaign,
            CampaignActivity campaignActivity,
            List<SubscriberAccount> subscribers, 
            List<Client> clientLists,
            List<SubscriptionListField> targetListFields) throws IncompleteDataException, DataValidationException, InvalidEmailException {
        
        int count = 0;
        if(subscribers.isEmpty())
            return 0;
        //Retrieve all unsubscribe codes
        Map<SubscriberAccount,String> unsubCodes = this.getUnsubscribeCodes(subscribers, clientLists.get(0).getOBJECTID()); //DB hit
        //Retrieve all subscriber's field values
        //List<SubscriberFieldValue> fieldValues = subService.getSubscriberValuesBySubscriberObjects(subscribers); //DB hit
        List<Long> subscrIds = objService.extractIds(subscribers);
        List<SubscriberFieldValue> fieldValues = objService.getEnterpriseDataByIds(subscrIds, SubscriberFieldValue.class);

        List<Long> subscriberIds = new ArrayList<>();
        for(SubscriberAccount subscriber : subscribers) {
            subscriberIds.add(subscriber.getOBJECTID());
        }
        Map<Long,Map<String,String>> fieldValuesMap = mmService.createMMValueMap(subscriberIds,targetListFields, fieldValues);

        //Declare instances here to avoid GC overflow?
        Email email;
        Document doc;
        Trigger_Email_Activity trigger;
        
        for (SubscriberAccount subscriber : subscribers) {
            email = new Email();
            //Set the header info of the email
            email.setSUBJECT(campaignActivity.getACTIVITY_NAME());
            email.setSENDER_ADDRESS(campaign.getOVERRIDE_SEND_AS_EMAIL());
            email.setSENDER_NAME(campaign.getOVERRIDE_SEND_AS_NAME());
            email.addRecipient(subscriber.getEMAIL());

            //Set the body of the email
            String content = campaignActivity.getACTIVITY_CONTENT_PROCESSED();
            content = mmService.parseUnsubscribeLink(content, unsubCodes.get(subscriber)); //we'll use the WS method to edit unsub links [update] not now, let's stick to hardcoding as there isn't enough time
            content = mmService.parseMailmergeTagsSubscriber(content, fieldValuesMap.get(subscriber.getOBJECTID()));

            //email.setBODY(content);
            /**
             * This is done here because we need to persist Email first to get 
             * a transaction key, and it will still get updated into the DB because
             * this is done within the transaction context.
             */
            doc = Jsoup.parse(content);
            appendSubscriberKeyToLinks(doc, subscriber);
            email.setBODY(doc.outerHtml());

            mailService.queueEmail(email, DateTime.now());

            trigger = new Trigger_Email_Activity();
            trigger.setTRIGGERED_TRANSACTION(email);
            trigger.setTRIGGERING_OBJECT(campaignActivity);

            updService.persist(trigger);

            count++;
        }
        return count;
    }
    
    /**
     * 
     * @param doc
     * @param subscriber 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void appendSubscriberKeyToLinks(Document doc, SubscriberAccount subscriber) {
        //String content = activity.getACTIVITY_CONTENT_PROCESSED();
        //Document doc = Jsoup.parse(email.getBODY());
        Elements links = doc.select("a[data-link]");
        
        for(int i=0; i<links.size(); i++) {
            Element link = links.get(i);
            String href = link.attr("href");
            
            if(!href.endsWith("/"))
                href += "/";
            href += subscriber.getOBJECTID();
            link.attr("href", href);
        }
    }
}
