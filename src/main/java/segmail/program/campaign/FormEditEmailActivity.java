/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import seca2.js.JSUtility;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEditEntity;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import static segmail.entity.campaign.ACTIVITY_STATUS.NEW;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivityOutboundLink;
import segmail.entity.campaign.CampaignActivitySchedule;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditEmailActivity")
public class FormEditEmailActivity implements FormEditEntity {

    @Inject
    ProgramCampaign program;

    @EJB
    CampaignService campaignService;

    List<CampaignActivityOutboundLink> links = new ArrayList<>();

    private String linksDelimited;

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

    public List<CampaignActivityOutboundLink> getLinks() {
        return links;
    }

    public void setLinks(List<CampaignActivityOutboundLink> links) {
        this.links = links;
    }

    public String getLinksDelimited() {
        return linksDelimited;
    }

    public void setLinksDelimited(String linksDelimited) {
        this.linksDelimited = linksDelimited;
    }

    @Override
    public void saveAndContinue() {
        try {

            campaignService.updateCampaignActivity(getEditingActivity());
            campaignService.updateCampaignActivitySchedule(getEditingSchedule());
            loadActivity(program.getEditingCampaignId());

            List<CampaignActivityOutboundLink> links = this.constructLinks(linksDelimited);

            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Email saved", "");
        } catch (IncompleteDataException ex) {
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
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Campaign activity deleted.", "");
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

    }

    private List<CampaignActivityOutboundLink> constructLinks(String linksText) {
        //String[] splitLinks = linksText.split(",");

        List<CampaignActivityOutboundLink> links = new ArrayList<>();
        if (linksText == null || linksText.isEmpty()) {
            return links;
        }
        JsonReader reader = Json.createReader(new StringReader(linksText));
        JsonArray linkObjs = reader.readArray();

        //Need to construct the CampaignActivityOutboundLinks ourselves
        for (int i = 0; i < linkObjs.size(); i++) {
            JsonObject linkObj = linkObjs.getJsonObject(i);
            Map<String, Object> linkMap = JSUtility.convertJsonObjectToMap((JsonObject) linkObj);

            CampaignActivityOutboundLink link = new CampaignActivityOutboundLink();
            link.setLINK_TARGET((String) linkMap.get("target"));
            link.setLINK_TEXT((String) linkMap.get("text"));
            link.setSNO(Integer.parseInt((String) linkMap.get("index")));

            links.add(link);
        }

        return links;
    }

}
