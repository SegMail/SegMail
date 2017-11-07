/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.GenericObjectService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.program.FormEditEntity;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.link.CampaignActivityOutboundLink;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormTrackEmailActivity")
public class FormTrackEmailActivity implements FormEditEntity  {
    @Inject UserRequestContainer reqCont;
    @Inject ProgramCampaign program;
    @Inject FormEditEmailActivity formEditEmailActivity;
    @Inject ClientContainer cltCont;
    
    @EJB CampaignService campService;
    @EJB GenericObjectService objService;
    
    private long totalTargeted;
    private long totalSent;
    private long totalClicked;
    private double conversionRate;
    private Map<CampaignActivityOutboundLink,Long> clicks;
    
    public CampaignActivity getEditingActivity() {
        return program.getEditingActivity();
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        program.setEditingActivity(editingActivity);
    }

    public long getTotalTargeted() {
        return totalTargeted;
    }

    public void setTotalTargeted(long totalTargeted) {
        this.totalTargeted = totalTargeted;
    }

    public long getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(long totalSent) {
        this.totalSent = totalSent;
    }

    public long getTotalClicked() {
        return totalClicked;
    }

    public void setTotalClicked(long totalClicked) {
        this.totalClicked = totalClicked;
    }

    public Map<CampaignActivityOutboundLink, Long> getClicks() {
        return clicks;
    }

    public void setClicks(Map<CampaignActivityOutboundLink, Long> clicks) {
        this.clicks = clicks;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }
    
    public boolean renderThis() {
        return reqCont.getPathParser().getOrderedParams().size() == 1;
    }

    @Override
    public void saveAndContinue() {
        setTotalTargeted(campService.countTargetedSubscribersForActivity(getEditingActivity().getOBJECTID(),cltCont.getClient().getOBJECTID()));
        setTotalSent(campService.countEmailsSentForActivity(getEditingActivity().getOBJECTID()));
        setTotalClicked(campService.countTotalClicksForActivity(getEditingActivity().getOBJECTID()));
        
        setConversionRate(loadConversionRate(getEditingActivity().getOBJECTID(), totalClicked));
        
        //Load clicks for each link
        setClicks(loadClicks(getEditingActivity().getOBJECTID()));
    }

    @Override
    public void saveAndClose() {
        this.saveAndContinue();
        this.closeWithoutSaving();
    }

    @Override
    public void closeWithoutSaving() {
        program.refresh();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void trackCampaignActivity(long campaignActivityId) {
        formEditEmailActivity.loadActivity(campaignActivityId);
        
        saveAndContinue();
    }
    
    public double loadConversionRate(long campaignActivityId, long totalClicks) {
        double clickthrough =  0.00;
        double uniqueClicks = campService.countConvertedEmails(campaignActivityId);
        
        if(totalSent > 0) {
            clickthrough = (uniqueClicks/totalSent) * 100.00;
        }
        BigDecimal rounded = new BigDecimal(clickthrough);
        clickthrough = rounded.setScale(3,RoundingMode.HALF_UP).doubleValue();
        
        return clickthrough;
    }
    
    public Map<CampaignActivityOutboundLink,Long> loadClicks(long campaignActivityId) {
        List<CampaignActivityOutboundLink> allLinks = objService.getEnterpriseData(campaignActivityId, CampaignActivityOutboundLink.class);
        
        Map<CampaignActivityOutboundLink,Long> results = new HashMap<>();
        allLinks.forEach(link -> {
        //for(CampaignActivityOutboundLink link : allLinks) {
            long count = campService.getLinkClicks(link.getLINK_KEY());
            results.put(link, count);
        });
        
        return results;
    }
}
