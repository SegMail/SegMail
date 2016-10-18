/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.mysettings;

import eds.entity.client.ContactInfo;
import eds.entity.client.VerifiedSendingAddress;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import seca2.program.Program;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
@Named("MySettingsProgram")
public class MySettingsProgram extends Program {
    
    private final String pageName = "my_settings_program";
    
    private ContactInfo contactInfo;
    private String userEmail;
    
    private List<VerifiedSendingAddress> addresses;
    private String deleteAddress;
    
    @PostConstruct
    public void init(){
        
    }
    
    public String getPageName() {
        return pageName;
    }


    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<VerifiedSendingAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<VerifiedSendingAddress> addresses) {
        this.addresses = addresses;
    }

    public String getDeleteAddress() {
        return deleteAddress;
    }

    public void setDeleteAddress(String deleteAddress) {
        this.deleteAddress = deleteAddress;
    }

    @Override
    public void clearVariables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initRequestParams() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initProgram() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
