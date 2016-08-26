/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.account;

import eds.entity.client.ClientUserAssignment;

/**
 *
 * @author LeeKiatHaw
 */
public class SegmailAccountContainer {
    
    //Input
    private String email;
    private String password;
    private boolean help;
    private String uploadedSubscriberListKey; //or a simpler listId?
    private long listId;
    
    //Output
    private ClientUserAssignment clientAssignment;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public String getUploadedSubscriberListKey() {
        return uploadedSubscriberListKey;
    }

    public void setUploadedSubscriberListKey(String uploadedSubscriberListKey) {
        this.uploadedSubscriberListKey = uploadedSubscriberListKey;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public ClientUserAssignment getClientAssignment() {
        return clientAssignment;
    }

    public void setClientAssignment(ClientUserAssignment clientAssignment) {
        this.clientAssignment = clientAssignment;
    }
    
    
}
