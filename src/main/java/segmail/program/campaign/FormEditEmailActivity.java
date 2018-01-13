/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.GenericObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEditEntity;
import segmail.component.campaign.CampaignService;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import static segmail.entity.campaign.ACTIVITY_STATUS.NEW;
import segmail.entity.campaign.Assign_CampaignActivity_List;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.link.CampaignActivityOutboundLink;
import segmail.entity.campaign.CampaignActivitySchedule;
import segmail.entity.campaign.filter.CampaignActivityFilter;
import segmail.entity.campaign.filter.FILTER_OPERATOR;
import segmail.entity.subscription.Assign_Client_List;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;
import segmail.program.autoresponder.webservice.AutoresponderSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditEmailActivity")
public class FormEditEmailActivity implements FormEditEntity {

    @Inject UserRequestContainer reqCont;
    @Inject ClientContainer clientCont;
    @Inject ProgramCampaign program;
    @Inject AutoresponderSessionContainer autoresponderCont;
    @Inject FormProgramModeSwitch programSwitch;

    @EJB
    CampaignService campaignService;
    @EJB
    SubscriptionService subService;
    @EJB
    MailMergeService mmService;
    @EJB
    GenericObjectService objService;
    @EJB
    private LandingService landingService;

    private String linksDelimited;

    @PostConstruct
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loadSchedule();
            loadOwnedLists();
            loadTargetLists();
            loadTargetListFields();
            loadFilters();
            loadRandomValues();
            checkServer();
            loadOutboundLinks();
            loadCampaignTags();
            loadExtraSubscriberTags();
            loadPreviewBody();
        }
    }

    public CampaignActivitySchedule getEditingSchedule() {
        return program.getEditingSchedule();
    }

    public void setEditingSchedule(CampaignActivitySchedule editingSchedule) {
        program.setEditingSchedule(editingSchedule);
    }

    public CampaignActivity getEditingActivity() {
        return program.getEditingActivity();
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        program.setEditingActivity(editingActivity);
    }

    public String getLinksDelimited() {
        return linksDelimited;
    }

    public void setLinksDelimited(String linksDelimited) {
        this.linksDelimited = linksDelimited;
    }

    public MAILMERGE_REQUEST[] getMailmergeLinkTags() {
        return program.getMailmergeLinkTags();
    }

    public void setMailmergeLinkTags(MAILMERGE_REQUEST[] mailmergeLinkTags) {
        program.setMailmergeLinkTags(mailmergeLinkTags);
    }

    public List<SubscriptionListField> getListFields() {
        return autoresponderCont.getFields();
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        autoresponderCont.setFields(listFields);
    }

    public Map<String, String> getRandomSubscriber() {
        return autoresponderCont.getRandomSubscriber();
    }

    public void setRandomSubscriber(Map<String, String> randomSubscriber) {
        autoresponderCont.setRandomSubscriber(randomSubscriber);
    }

    public List<SubscriptionList> getTargetLists() {
        return program.getTargetLists();
    }

    public void setTargetLists(List<SubscriptionList> targetLists) {
        program.setTargetLists(targetLists);
    }

    public Map<String, String> getMailmergeLinks() {
        return program.getMailmergeLinks();
    }

    public void setMailmergeLinks(Map<String, String> mailmergeLinks) {
        program.setMailmergeLinks(mailmergeLinks);
    }
    
    public Map<String, List<String>> getMailmergeListFields() {
        return program.getMailmergeListFields();
    }

    public void setMailmergeListFields(Map<String, List<String>> mailmergeListFields) {
        program.setMailmergeListFields(mailmergeListFields);
    }
    
    public Campaign getEditingCampaign() {
        return program.getEditingCampaign();
    }

    public void setEditingCampaign(Campaign editingCampaign) {
        program.setEditingCampaign(editingCampaign);
    }
    
    public List<CampaignActivityOutboundLink> getLinks() {
        return program.getLinks();
    }

    public void setLinks(List<CampaignActivityOutboundLink> links) {
        program.setLinks(links);
    }
    
    public List<String> getCampaignTags() {
        return program.getCampaignTags();
    }

    public void setCampaignTags(List<String> campaignTags) {
        program.setCampaignTags(campaignTags);
    }
    
    public List<String> getExtraSubscriberTags() {
        return program.getExtraSubscriberTags();
    }

    public void setExtraSubscriberTags(List<String> extraSubscriberTags) {
        program.setExtraSubscriberTags(extraSubscriberTags);
    }
    
    public String getPreviewBody() {
        return program.getPreviewBody();
    }

    public void setPreviewBody(String previewBody) {
        program.setPreviewBody(previewBody);
    }
    
    public List<CampaignActivityFilter> getFilters() {
        return program.getFilters();
    }

    public void setFilters(List<CampaignActivityFilter> filters) {
        program.setFilters(filters);
    }
    
    public List<SubscriptionList> getOwnedLists() {
        return program.getOwnedLists();
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        program.setOwnedLists(ownedLists);
    }
    
    public List<String> getSelectedTargetLists() {
        return program.getSelectedTargetLists();
    }

    public void setSelectedTargetLists(List<String> selectedTargetLists) {
        program.setSelectedTargetLists(selectedTargetLists);
    }
    
    public FILTER_OPERATOR[] getOperators() {
        return program.getOperators();
    }
    
    public boolean renderThis() {
        return reqCont.getPathParser().getOrderedParams().size() == 2;
    }

    @Override
    public void saveAndContinue() {
        try {
            
            CampaignActivity activity = campaignService.updateCampaignActivity(getEditingActivity());
            setEditingActivity(activity);
            CampaignActivitySchedule schedule = campaignService.updateCampaignActivitySchedule(getEditingSchedule());
            setEditingSchedule(schedule);
            
            // Update targeted lists
            campaignService.assignTargetListToCampaign(
                    getSelectedTargetLists().stream().map(l -> Long.parseLong(l)).collect(toList())
                    ,activity.getOBJECTID()
                    ,clientCont.getClient().getOBJECTID()
            );
            loadTargetLists();
            loadTargetListFields();
            
            // Update Filters
            campaignService.updateFilters(getEditingActivity().getOBJECTID(),getFilters());
            loadFilters(); //Needed?
            
            //Reload Campaign and CampaignActivity as they have been updated
            //programSwitch.reloadEntities();
            //Reload all OutboundLinks for the activity
            loadOutboundLinks();
            loadPreviewBody();
            

            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Email saved", "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }

    @Override
    public void saveAndClose() {
        saveAndContinue();
        closeWithoutSaving();
    }

    @Override
    public void closeWithoutSaving() {
        program.refresh();
    }

    @Override
    public void delete() {
        try {
            campaignService.deleteCampaignActivity(getEditingActivity().getOBJECTID());
            FacesMessenger.setFacesMessage(ProgramCampaign.class.getSimpleName(), FacesMessage.SEVERITY_FATAL, "Campaign activity deleted.", "");
            closeWithoutSaving();
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }

    public boolean canEdit() {
        if (getEditingActivity() == null) {
            return false;
        }
        switch (ACTIVITY_STATUS.valueOf(getEditingActivity().getSTATUS())) {
            case NEW:
                return true;
            case EXECUTING:
                return false;
            case COMPLETED:
                return false;
            case STOPPED:
                return false;
            default:
                return false;
        }
    }

    public void loadActivity(long activityId) {
        CampaignActivity act = campaignService.getCampaignActivity(activityId);
        program.setEditingActivity(act);

        CampaignActivitySchedule schedule = campaignService.getCampaignActivitySchedule(activityId);
        program.setEditingSchedule(schedule);

        programSwitch.reloadEntities();
    }

    public void loadTargetListFields() {
        List<SubscriptionListField> fieldList = campaignService.getTargetedListFieldsForActivity(getEditingActivity().getOBJECTID());

        //Only add those fields that are common across the 2 lists
        Collections.sort(fieldList, new Comparator<SubscriptionListField>() {

            @Override
            public int compare(SubscriptionListField o1, SubscriptionListField o2) {
                if (o1.getMAILMERGE_TAG() == null || o1.getMAILMERGE_TAG().isEmpty()) {
                    return -1;
                }

                if (o2.getMAILMERGE_TAG() == null || o2.getMAILMERGE_TAG().isEmpty()) {
                    return 1;
                }

                return o1.getMAILMERGE_TAG().compareTo(o2.getMAILMERGE_TAG());
            }
        });
        List<SubscriptionListField> sortedList = fieldList;//new ArrayList<>();

        setListFields(sortedList);
        
        //Set another Multivalued Map for fields
        setMailmergeListFields(new HashMap<String,List<String>>());
        for(SubscriptionListField field : sortedList) {
            if(!getMailmergeListFields().containsKey(field.getMAILMERGE_TAG())) {
                getMailmergeListFields().put(field.getMAILMERGE_TAG(), new ArrayList<String>());
            }
            getMailmergeListFields().get(field.getMAILMERGE_TAG()).add((String) field.generateKey());
        }
    }

    public void loadRandomValues() {
        //Clear it first
        setRandomSubscriber(null);

        if (this.getEditingActivity() == null) {
            return;
        }

        List<SubscriptionList> targetLists = getTargetLists();
        if (targetLists == null || targetLists.isEmpty()) {
            return; //Just let the WS return the labels without converting
        }
        //Collect field values from all targeted lists
        //Actually the identification doesn't matter
        Map<String, String> randomValues = new HashMap<>();
        for (SubscriptionList targetList : targetLists) {
            Map<Long,Map<String, String>> subscriber = subService.getSubscriberValuesMap(targetList.getOBJECTID(), 0, 1);
            if (!subscriber.isEmpty()) {
                //Extract individual subscriber (will only contain <= 1)
                for(Map<String, String> sub : subscriber.values()) {
                    randomValues.putAll(sub);
                    break;
                }
            }
        }

        setRandomSubscriber(randomValues);
    }

    public void loadOwnedLists() {
        long clientId = clientCont.getClient().getOBJECTID();
        
        List<SubscriptionList> ownedList = objService.getAllTargetObjectsFromSource(clientId, Assign_Client_List.class, SubscriptionList.class);
        
        setOwnedLists(ownedList);
    }
    
    public void loadTargetLists() {
        CampaignActivity editingActivity = getEditingActivity();

        List<SubscriptionList> targetList = objService.getAllTargetObjectsFromSource(
                editingActivity.getOBJECTID(), 
                Assign_CampaignActivity_List.class, 
                SubscriptionList.class
        );
        setTargetLists(targetList);
        setSelectedTargetLists(
                targetList.stream().map(l -> ""+l.getOBJECTID()).collect(toList()));
    }

    public void loadSchedule() {
        CampaignActivity editingActivity = getEditingActivity();

        CampaignActivitySchedule schedule = campaignService.getCampaignActivitySchedule(editingActivity.getOBJECTID());
        program.setEditingSchedule(schedule);
    }

    public void checkServer() {

        try {
            ServerInstance testServer = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.WEB);
            if(testServer == null)
                throw new IncompleteDataException("Test server is not setup properly. Please contact your system admin.");
        
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }

    }
    
    public void loadOutboundLinks() {
        if(getEditingActivity() == null) 
            return;
        
        List<CampaignActivityOutboundLink> links = objService.getEnterpriseData(getEditingActivity().getOBJECTID(), CampaignActivityOutboundLink.class);
        setLinks(links);
    }
    
    public void loadCampaignTags() {
        if(getEditingActivity() == null) 
            return;
        
        List<String> campaignTags = new ArrayList<>();
        campaignTags.add(Campaign.MM_SENDER_NAME);
        campaignTags.add(Campaign.MM_SUPPORT_EMAIL);
        
        setCampaignTags(campaignTags);
    }
    
    public void loadExtraSubscriberTags() {
        if(getEditingActivity() == null) 
            return;
        
        List<String> extraSubscriberTags = new ArrayList<>();
        extraSubscriberTags.add(SubscriberAccount.MM_DATE_OF_SUBSCRIPTION);
        extraSubscriberTags.add(SubscriberAccount.MM_LENGTH_OF_SUBSCRIPTION);
        
        setExtraSubscriberTags(extraSubscriberTags);
    }
    
    public void loadPreviewBody() {
        if(getEditingActivity() == null) 
            return;
        CampaignActivity activity = getEditingActivity();
        
        // So far only the rendering of random subscriber
        String body = campaignService.parseRandomSubscriber(activity.getACTIVITY_CONTENT_PROCESSED(),getRandomSubscriber(),getListFields());
        
        setPreviewBody(body);
    }
    
    public void loadFilters() {
        CampaignActivity activity = getEditingActivity();
        List<CampaignActivityFilter> filters = objService.getEnterpriseData(activity.getOBJECTID(), CampaignActivityFilter.class);
        
        setFilters(filters.stream().sorted((a,b) -> {
           return a.getSNO() - b.getSNO();
        }).collect(toList()));
    }
    
    public void addNewFilter() {
        List<CampaignActivityFilter> filters = getFilters();
        // Assuming it's already sorted
        int lastSNO = (filters == null || filters.isEmpty()) ? 0 : filters.get(filters.size()-1).getSNO() + 1;
        CampaignActivityFilter newFilter = new CampaignActivityFilter();
        newFilter.setOWNER(getEditingActivity());
        newFilter.setSNO(filters.size() + 1);
        
        filters.add(newFilter);
        
    }
    
    public void deleteFilter(int sno) {
        List<CampaignActivityFilter> filters = getFilters();
        filters.removeIf(f -> f.getSNO() == sno);
        
    }
    
}
