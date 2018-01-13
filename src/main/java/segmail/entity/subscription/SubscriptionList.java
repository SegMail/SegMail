/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST")
public class SubscriptionList extends EnterpriseObject {// implements MailSender {
    
    public static final String MM_SUPPORT_EMAIL = "{!support}";
    public static final String MM_SENDER_NAME = "{!sender}";
    public static final String MM_DELIMITER = "|";
    
    /**
     * Private name viewable only by the list owners
     */
    private String LIST_NAME;
    
    /**
     * The email displayed in the From field when viewed by the recipient.
     * 
     * This is only used in sending AutoresponderEmails, not CampaignEmailActivity.
     */
    private String SEND_AS_EMAIL;
    
    /**
     * The name displayed in the From field when viewed by the recipient.
     * 
     * This is only used in sending AutoresponderEmails, not CampaignEmailActivity.
     */
    private String SEND_AS_NAME;
    
    /**
     * Indicates if the list requires double opt-in method of confirming subscribers.
     * It is by default TRUE.
     */
    private boolean DOUBLE_OPTIN;
    
    /**
     * Indicates if the list is stored in a remote location instead of in the 
     * local server.
     */
    private boolean REMOTE;
    
    /**
     * Current number of subscribers by subscription.
     */
    @Deprecated
    private long SUBSCRIBER_COUNT;
    
    /**
     * The address the recipient is sending to when they click "Reply" in the 
     * email.
     */
    private String REPLY_TO_ADDRESS;
    
    /**
     * After double opt-in, if users were to be redirected to another site
     */
    private String REDIRECT_CONFIRM;
    
    private String REDIRECT_WELCOME;
    
    private String REDIRECT_UNSUBSCRIBE;
    
    /**
     * "|"-delimited string of mailmerge tags
     */
    private String REDIRECT_CONFIRM_PARAMS;
    private String REDIRECT_WELCOME_PARAMS;
    private String REDIRECT_UNSUBSCRIBE_PARAMS;
    
    /**
     * The email address that bounces are returned to. 
     * 
     * We will not use this at the moment, but SES own bounce handling instead:
     * https://sesblog.amazon.com/post/TxJE1JNZ6T9JXK/Handling-Bounces-and-Complaints
     */
    //private String RETURN_PATH;
    
    private String SUPPORT_EMAIL;

    public SubscriptionList() {
        this.REMOTE = true; //Default value
        this.DOUBLE_OPTIN = true;
    }
    
    
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String alias() {
        return this.LIST_NAME;
    }

    public String getLIST_NAME() {
        return LIST_NAME;
    }

    public void setLIST_NAME(String LIST_NAME) {
        this.LIST_NAME = LIST_NAME;
    }

    public String getSEND_AS_EMAIL() {
        return SEND_AS_EMAIL;
    }

    public void setSEND_AS_EMAIL(String SEND_AS_EMAIL) {
        this.SEND_AS_EMAIL = SEND_AS_EMAIL;
    }

    public String getSEND_AS_NAME() {
        return SEND_AS_NAME;
    }

    public void setSEND_AS_NAME(String SEND_AS_NAME) {
        this.SEND_AS_NAME = SEND_AS_NAME;
    }

    
    public boolean isDOUBLE_OPTIN() {
        return DOUBLE_OPTIN;
    }

    public void setDOUBLE_OPTIN(boolean DOUBLE_OPTIN) {
        this.DOUBLE_OPTIN = DOUBLE_OPTIN;
    }

    public boolean isREMOTE() {
        return REMOTE;
    }

    public void setREMOTE(boolean REMOTE) {
        this.REMOTE = REMOTE;
    }

    @Deprecated
    public long getSUBSCRIBER_COUNT() {
        return SUBSCRIBER_COUNT;
    }

    @Deprecated
    public void setSUBSCRIBER_COUNT(long SUBSCRIBER_COUNT) {
        this.SUBSCRIBER_COUNT = SUBSCRIBER_COUNT;
    }

    public String getREPLY_TO_ADDRESS() {
        return REPLY_TO_ADDRESS;
    }

    public void setREPLY_TO_ADDRESS(String REPLY_TO_ADDRESS) {
        this.REPLY_TO_ADDRESS = REPLY_TO_ADDRESS;
    }

    public String getREDIRECT_CONFIRM() {
        return REDIRECT_CONFIRM;
    }

    public void setREDIRECT_CONFIRM(String REDIRECT_CONFIRM) {
        this.REDIRECT_CONFIRM = REDIRECT_CONFIRM;
    }

    public String getREDIRECT_WELCOME() {
        return REDIRECT_WELCOME;
    }

    public void setREDIRECT_WELCOME(String REDIRECT_WELCOME) {
        this.REDIRECT_WELCOME = REDIRECT_WELCOME;
    }

    public String getREDIRECT_UNSUBSCRIBE() {
        return REDIRECT_UNSUBSCRIBE;
    }

    public void setREDIRECT_UNSUBSCRIBE(String REDIRECT_UNSUBSCRIBE) {
        this.REDIRECT_UNSUBSCRIBE = REDIRECT_UNSUBSCRIBE;
    }

    public String getSUPPORT_EMAIL() {
        return SUPPORT_EMAIL;
    }

    public void setSUPPORT_EMAIL(String SUPPORT_EMAIL) {
        this.SUPPORT_EMAIL = SUPPORT_EMAIL;
    }

    public String getREDIRECT_CONFIRM_PARAMS() {
        return REDIRECT_CONFIRM_PARAMS;
    }

    public void setREDIRECT_CONFIRM_PARAMS(String REDIRECT_CONFIRM_PARAMS) {
        this.REDIRECT_CONFIRM_PARAMS = REDIRECT_CONFIRM_PARAMS;
    }
    
    public void setREDIRECT_CONFIRM_PARAMS(List<String> params) {
        setREDIRECT_CONFIRM_PARAMS(generateUrlParamQuery(params));
    }

    public String getREDIRECT_WELCOME_PARAMS() {
        return REDIRECT_WELCOME_PARAMS;
    }

    public void setREDIRECT_WELCOME_PARAMS(String REDIRECT_WELCOME_PARAMS) {
        this.REDIRECT_WELCOME_PARAMS = REDIRECT_WELCOME_PARAMS;
    }
    
    public void setREDIRECT_WELCOME_PARAMS(List<String> params) {
        setREDIRECT_WELCOME_PARAMS(generateUrlParamQuery(params));
    }

    public String getREDIRECT_UNSUBSCRIBE_PARAMS() {
        return REDIRECT_UNSUBSCRIBE_PARAMS;
    }

    public void setREDIRECT_UNSUBSCRIBE_PARAMS(String REDIRECT_UNSUBSCRIBE_PARAMS) {
        this.REDIRECT_UNSUBSCRIBE_PARAMS = REDIRECT_UNSUBSCRIBE_PARAMS;
    }
    
    public void setREDIRECT_UNSUBSCRIBE_PARAMS(List<String> params) {
        setREDIRECT_UNSUBSCRIBE_PARAMS(generateUrlParamQuery(params));
    }
    
    /**
     * This is just for display purposes. During actual sending/rendering of mailmerge
     * tags, we would not be using this method.
     * 
     * @param url
     * @param params
     * @param delimiter
     * @return
     * @throws URISyntaxException 
     */
    public String generatePlaceholderUrl(String url, String params, String delimiter) throws URISyntaxException{
        if(url == null || url.isEmpty())
            return url;
        
        // Clean the url of parameters first
        if(!url.startsWith("http")) {
            url = "http://" + url;
        }
        URIBuilder builder = new URIBuilder(url);
        // Don't clear params, if url already contains param, add on top of it
        for(String param : params.split("\\"+delimiter)) {
            if(param == null || param.isEmpty()) 
                continue;
            String paramKey = param;
            if(paramKey.startsWith("{"))
                paramKey = paramKey.substring(1);
            if(paramKey.endsWith("}"))
                paramKey = paramKey.substring(0, paramKey.length()-1);

            builder.addParameter(paramKey, param);
        }
        
        URI uri = builder.build();
        
        String finalUrl = uri.getScheme()+"://"+uri.getHost()+uri.getPath();
        if(uri.getQuery() != null && !uri.getQuery().isEmpty())
            finalUrl += "?" + uri.getQuery();
        return finalUrl;
    }
    
    public String generateConfirmUrl() throws URISyntaxException {
        return this.generatePlaceholderUrl(
                getREDIRECT_CONFIRM(), 
                getREDIRECT_CONFIRM_PARAMS(), 
                MM_DELIMITER);
    }
    
    public String generateWelcomeUrl() throws URISyntaxException {
        return this.generatePlaceholderUrl(
                getREDIRECT_WELCOME(), 
                getREDIRECT_WELCOME_PARAMS(), 
                MM_DELIMITER);
    }
    
    public String generateUnsubscribeUrl() throws URISyntaxException {
        return this.generatePlaceholderUrl(
                getREDIRECT_UNSUBSCRIBE(), 
                getREDIRECT_UNSUBSCRIBE_PARAMS(), 
                MM_DELIMITER);
    }
    
    public String generateUrlParamQuery (List<String> params) {
        String query = "";
        for(String tag : params) {
            if(query.length() > 0) 
                query += MM_DELIMITER;
            query += tag;
        }
        return query;
    }
    
    public List<String> generateConfirmParamList() {
        return generateUrlParamList(getREDIRECT_CONFIRM_PARAMS());
    }
    
    public List<String> generateWelcomeParamList() {
        return generateUrlParamList(getREDIRECT_WELCOME_PARAMS());
    }
    
    public List<String> generateUnsubscribeParamList() {
        return generateUrlParamList(getREDIRECT_UNSUBSCRIBE_PARAMS());
    }
    
    public List<String> generateUrlParamList (String query) {
        String[] params = query.split("\\"+MM_DELIMITER);
        List<String> list = new ArrayList<>();
        for(String param : params) {
            list.add(param);
        }
        return list;
    }
}
