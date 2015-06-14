/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.test.client.FormRegisterClientForObjectname;
import seca2.program.test.client.FormRegisterClientType;
import seca2.program.test.layout.FormAssignLayoutProgram;
import seca2.program.test.layout.FormAssignLayoutUserType;
import seca2.program.test.layout.FormAssignLayoutUsername;
import seca2.program.test.layout.FormCreateLayout;

/**
 * Note: This is not a test, but a setup!
 * 
 * @author LeeKiatHaw
 */
@Named("FormTestEverything")
@RequestScoped
public class FormTestEverything {
    
    @Inject private ProgramTest programTest;
    @Inject private FormTestDB formTestDB;
    @Inject private FormTestNavigation formTestNavigation;
    @Inject private FormTestUser formTestUser;
    @Inject private FormTestProgram formTestProgram;
    @Inject private FormCreateLayout formCreateLayout;
    @Inject private FormAssignLayoutUsername formAssignLayoutUsername;
    @Inject private FormAssignLayoutUserType formAssignLayoutUserType;
    @Inject private FormAssignLayoutProgram formAssignLayoutProgram;
    @Inject private FormRegisterClientType formRegisterClientType;
    @Inject private FormRegisterClientForObjectname formRegisterClientForObjectname;
    
    // Setup variables
    
    private final String ADMIN_USERTYPE_SEGMAIL = "Segmail Administrator";
    private final String ADMIN_USERNAME_SEGMAIL = "sadmin";
    private final String ADMIN_PASSWORD_SEGMAIL = "sadmin";
    
    private final String ADMIN_USERTYPE_TM = "TM Administrator";
    private final String ADMIN_USERNAME_TM = "tmadmin";
    private final String ADMIN_PASSWORD_TM = "tmadmin";
    
    private final String ADMIN_USERTYPE_CHARTJS = "ChartJS Administrator";
    private final String ADMIN_USERNAME_CHARTJS = "chartjsadmin";
    private final String ADMIN_PASSWORD_CHARTJS = "chartjsadmin";
    
    private final String CLIENT_TYPE_PERSON = "Person";
    
    // File upload
    private Part file;
    
    private UploadedFile uploadedFile;
    
    private final String USERTYPES_TAG = "USERTYPES";
    private final String USERTYPE_TAG = "USERTYPE";
    
    private final String USERS_TAG = "USERS";
    private final String USER_TAG = "USER";
    private final String USER_NAME_TYPE_TAG = "SELECTED_USERTYPE";
    private final String USER_NAME_TAG = "USERNAME";
    private final String USER_PW_TAG = "PASSWORD";
    
    private final String PROGRAMS_TAG = "PROGRAMS";
    private final String PROGRAM_TAG = "PROGRAM";
    private final String PROGRAM_NAME_TAG = "PROGRAM_NAME";
    private final String PROGRAM_VIEW_TAG = "PROGRAM_VIEWROOT";
    private final String PROGRAM_DISP_TAG = "PROGRAM_DISPLAY_NAME";
    private final String PROGRAM_DESC_TAG = "PROGRAM_DISPLAY_DESC";
    
    private final String MENU_TAG = "MENU";
    private final String MENU_NAME_TAG = "MENU_NAME";
    private final String MENU_URL_TAG = "MENU_URL";
    private final String MENU_PREPEND_TAG = "MENU_PREPEND";
    
    private final String MENU_ASSIGN_TAG = "MENU_ASSIGN";
    private final String MENU_ASSIGN_USERTYPE_TAG = "MENU_ASSIGN_USERTYPE";
    private final String MENU_ASSIGN_MENU_TAG = "MENU_ASSIGN_MENU";
    
    private final String PROGRAM_ASSIGN_TAG = "PROGRAM_ASSIGN";
    private final String PROGRAM_ASSIGN_PROGRAM_TAG = "PROGRAM_ASSIGN_PROGRAM";
    private final String PROGRAM_ASSIGN_USERTYPE_TAG = "PROGRAM_ASSIGN_USERTYPE";
    
    public void init(){
        System.out.println("Test everything init");
    }
    
    public void setupSegmail(){
        //Setup DB
        formTestDB.generateDB();
        
        //Create usertypes
        this.formTestUser.setUserTypeName(ADMIN_USERTYPE_SEGMAIL);
        this.formTestUser.createUserType();
        this.formTestUser.setUserTypeName(ADMIN_USERTYPE_TM);
        this.formTestUser.createUserType();
        this.formTestUser.setUserTypeName(ADMIN_USERTYPE_CHARTJS);
        this.formTestUser.createUserType();
        
        programTest.init();
        //UserType administrator = this.programTest.getAllUserTypes().get(0);
        
        //Create user
        //this.formTestUser.setChosenUserType(administrator.getOBJECTID());
        //this.formTestUser.setUADMIN_USERNAME_SEGMAILUSERNAME);
        //this.formTestUser.setPassword("admin");
        //this.formTestUser.createUser();
        this.formTestUser.createUserWithType(ADMIN_USERTYPE_TM, ADMIN_USERNAME_TM, ADMIN_PASSWORD_TM);
        this.formTestUser.createUserWithType(ADMIN_USERTYPE_SEGMAIL, ADMIN_USERNAME_SEGMAIL, ADMIN_PASSWORD_SEGMAIL);
        this.formTestUser.createUserWithType(ADMIN_USERTYPE_CHARTJS, ADMIN_USERNAME_CHARTJS, ADMIN_PASSWORD_CHARTJS);
        
        //Create testing page
        this.formTestProgram.setProgramName("test");
        this.formTestProgram.setProgramViewRoot("/programs/test/layout.xhtml");
        this.formTestProgram.setDisplayName("Testing page");
        this.formTestProgram.setDisplayDesc("This is for you administrator to set up the DB.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Testing page");
        this.formTestNavigation.setMenuItemURL("/program/test/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-dashboard\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Testing page");
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Testing page");
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Testing page");
        
        //Creat chartjs page
        this.formTestProgram.setProgramName("chartjs");
        this.formTestProgram.setProgramViewRoot("/programs/chartjs/layout.xhtml");
        this.formTestProgram.setDisplayName("ChartJS");
        this.formTestProgram.setDisplayDesc("This is a showcase of the ChartJS tool and using JS to call a Java web service.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("ChartJS");
        this.formTestNavigation.setMenuItemURL("/program/chartjs/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-signal\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "ChartJS");
        
        //Create list page
        this.formTestProgram.setProgramName("list");
        this.formTestProgram.setProgramViewRoot("/programs/list/layout.xhtml");
        this.formTestProgram.setDisplayName("Manage Lists");
        this.formTestProgram.setDisplayDesc("Manage your mailing lists here.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Manage Lists");
        this.formTestNavigation.setMenuItemURL("/program/list/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-list\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Lists");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Testing page");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Testing page");
        
        //Create manage signup page
        this.formTestProgram.setProgramName("signup");
        this.formTestProgram.setProgramViewRoot("/programs/signup/layout.xhtml");
        this.formTestProgram.setDisplayName("Signup Forms");
        this.formTestProgram.setDisplayDesc("Manage your signup forms for your lists.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Manage Signup Forms");
        this.formTestNavigation.setMenuItemURL("/program/signup/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-code\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Signup Forms");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Testing page");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Testing page");
        
        //Create Template page
        this.formTestProgram.setProgramName("template");
        this.formTestProgram.setProgramViewRoot("/programs/emailtemplate/layout.xhtml");
        this.formTestProgram.setDisplayName("Manage Templates");
        this.formTestProgram.setDisplayDesc("Manage all your email templates here.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Manage Templates");
        this.formTestNavigation.setMenuItemURL("/program/template/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-file-text-o\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Templates");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Testing page");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Testing page");
        
        //Create mysettings page
        this.formTestProgram.setProgramName("mysettings");
        this.formTestProgram.setProgramViewRoot("/programs/mysettings/layout.xhtml");
        this.formTestProgram.setDisplayName("My settings");
        this.formTestProgram.setDisplayDesc("This is where you manage all your personal stuff.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("My settings");
        this.formTestNavigation.setMenuItemURL("/program/mysettings/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-cogs\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "My settings");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Testing page");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Testing page");
        
        //Create Manage Talent Profile page
        this.formTestProgram.setProgramName("profile");
        this.formTestProgram.setProgramViewRoot("/programs/profile/layout.xhtml");
        this.formTestProgram.setDisplayName("Talent Profile");
        this.formTestProgram.setDisplayDesc("See cool awesome people here.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Manage Talent Profile");
        this.formTestNavigation.setMenuItemURL("/program/profile/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-user\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Talent Profile");
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Manage Talent Profile");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Manage Talent Profile");
        
        //Create Annual Peformance and Potential Review page
        this.formTestProgram.setProgramName("review");
        this.formTestProgram.setProgramViewRoot("/programs/review/layout.xhtml");
        this.formTestProgram.setDisplayName("Submit Annual Performance & Potential Review");
        this.formTestProgram.setDisplayDesc("A chance to give nice and helpful feedback to cool and awesome people.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Submit Performance Review");
        this.formTestNavigation.setMenuItemURL("/program/review/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-coffee\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Talent Profile");
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Submit Performance Review");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Manage Talent Profile");
        
        //Create Manage Enterprise Structure page
        this.formTestProgram.setProgramName("enterprise");
        this.formTestProgram.setProgramViewRoot("/programs/enterprise/layout.xhtml");
        this.formTestProgram.setDisplayName("Manage Enterprise Structure");
        this.formTestProgram.setDisplayDesc("Manage who reports to who.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Manage Enterprise Structure");
        this.formTestNavigation.setMenuItemURL("/program/enterprise/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-building\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Talent Profile");
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Manage Enterprise Structure");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Manage Talent Profile");
        
        //Create Define Talent Attributes page
        this.formTestProgram.setProgramName("talentattributes");
        this.formTestProgram.setProgramViewRoot("/programs/talentattributes/layout.xhtml");
        this.formTestProgram.setDisplayName("Define Talent Attributes");
        this.formTestProgram.setDisplayDesc("Determine how many levels of difficulty to set for your people.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Define Talent Attributes");
        this.formTestNavigation.setMenuItemURL("/program/talentattributes/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-cogs\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Talent Profile");
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Define Talent Attributes");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Manage Talent Profile");
        
        //Create Plan Succession page
        this.formTestProgram.setProgramName("succesion");
        this.formTestProgram.setProgramViewRoot("/programs/succesion/layout.xhtml");
        this.formTestProgram.setDisplayName("Plan Succession");
        this.formTestProgram.setDisplayDesc("Determine the next better player.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Plan Succession");
        this.formTestNavigation.setMenuItemURL("/program/succesion/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-user-plus\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        // Assign them to the respective users
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_SEGMAIL, "Manage Talent Profile");
        this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_TM, "Plan Succession");
        //this.formTestNavigation.assignMenuItems(ADMIN_USERTYPE_CHARTJS, "Manage Talent Profile");
        
        //Create layouts
        this.formCreateLayout.setLayoutName("Beprobootstrap");
        this.formCreateLayout.setViewRoot("/templates/beprobootstrap/template-layout.xhtml");
        this.formCreateLayout.registerLayout();
        
        this.formCreateLayout.setLayoutName("Flat-ui");
        this.formCreateLayout.setViewRoot("/templates/mytemplate/template-layout.xhtml");
        this.formCreateLayout.registerLayout();
        
        programTest.init();
        
        this.formRegisterClientType.setClientType("Organization");
        this.formRegisterClientType.setClientTypeDesc("Represents an organization.");
        this.formRegisterClientType.registerClientType();
        
        this.formRegisterClientType.setClientType(CLIENT_TYPE_PERSON);
        this.formRegisterClientType.setClientTypeDesc("Represents a human being.");
        this.formRegisterClientType.registerClientType();
        
        // Assign all programs to all Admins
        this.formTestProgram.assignAllProgramsToUser(ADMIN_USERTYPE_SEGMAIL);
        this.formTestProgram.assignAllProgramsToUser(ADMIN_USERTYPE_TM);
        this.formTestProgram.assignAllProgramsToUser(ADMIN_USERTYPE_CHARTJS);
        
        // Create a client for Admin
        this.formRegisterClientForObjectname.registerClientForUser(CLIENT_TYPE_PERSON, ADMIN_USERTYPE_SEGMAIL);
        this.formRegisterClientForObjectname.registerClientForUser(CLIENT_TYPE_PERSON, ADMIN_USERTYPE_TM);
        this.formRegisterClientForObjectname.registerClientForUser(CLIENT_TYPE_PERSON, ADMIN_USERTYPE_CHARTJS);
        
    }
    
    public void uploadXMLPlain(){
        try{
            this.setupXML(file.getInputStream());
            
        } catch (IOException ioex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "File error.", ioex.getMessage());
        }
    }
    
    public void uploadXMLPF(FileUploadEvent event){
        try{
            InputStream is = event.getFile().getInputstream();
            this.setupXML(is);
            
        } catch (IOException ioex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "File error.", ioex.getMessage());
        }
    }
    
    public void uploadPF(){
        try {
            InputStream is = this.uploadedFile.getInputstream();
            this.setupXML(is);
        } catch (IOException ioex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "File error.", ioex.getMessage());
        }
    }
    
    public void setupXML(InputStream is){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            
            doc.getDocumentElement().normalize();
            
            // Set up usertypes first
            NodeList usertypes = doc.getElementsByTagName(USERTYPE_TAG);
            
            for(int n=0; n<usertypes.getLength(); n++){
                Node nNode = usertypes.item(n);
                Element element = (Element) nNode;
                //if(element.getNodeName().compareTo(USERTYPE_TAG) != 0)
                //    throw new RuntimeException("Wrong tag <"+element.getNodeName()+"> should be <"+USERTYPE_TAG+">");
                
                String usertypeName = element.getTextContent();
                
                this.formTestUser.setUserTypeName(usertypeName);
                this.formTestUser.createUserType();
            }
            
            // Create users
            NodeList users = doc.getElementsByTagName(USER_TAG);
            
            for(int n=0; n<users.getLength(); n++){
                Node nNode = users.item(n);
                Element element = (Element) nNode;
                //if(element.getNodeName().compareTo(USER_TAG) != 0)
                //    throw new RuntimeException("Wrong tag <"+element.getNodeName()+"> should be <"+USER_TAG+">");
                
                String usertype = element.getElementsByTagName(USER_NAME_TYPE_TAG).item(0).getTextContent();
                String username = element.getElementsByTagName(USER_NAME_TAG).item(0).getTextContent();
                String password = element.getElementsByTagName(USER_PW_TAG).item(0).getTextContent();
                
                this.formTestUser.createUserWithType(usertype, username, password);
            }
            
            // Create programs
            NodeList programs = doc.getElementsByTagName(PROGRAMS_TAG);
            
            for(int n=0; n<programs.getLength(); n++){
                Node nNode = programs.item(n);
                Element element = (Element) nNode;
                if(element.getNodeName().compareTo(PROGRAM_TAG) != 0)
                    throw new RuntimeException("Wrong tag <"+element.getNodeName()+"> should be <"+PROGRAM_TAG+">");
                
                String name = element.getAttribute(PROGRAM_NAME_TAG);
                String viewroot = element.getAttribute(PROGRAM_VIEW_TAG);
                String dispName = element.getAttribute(PROGRAM_DISP_TAG);
                String dispDesc = element.getAttribute(PROGRAM_DESC_TAG);
                
                this.formTestProgram.setProgramName(name);
                this.formTestProgram.setProgramViewRoot(viewroot);
                this.formTestProgram.setDisplayName(dispName);
                this.formTestProgram.setDisplayDesc(dispDesc);
                this.formTestProgram.createProgram();
            }
            
            // Create menuitems
            NodeList menuitems = doc.getElementsByTagName(MENU_TAG);
            
            for(int n=0; n<menuitems.getLength(); n++){
                Node nNode = menuitems.item(n);
                Element element = (Element) nNode;
                String name = element.getAttribute(MENU_NAME_TAG);
                String url = element.getAttribute(MENU_URL_TAG);
                String preprend = element.getAttribute(MENU_PREPEND_TAG);
                
                this.formTestNavigation.setMenuItemName(name);
                this.formTestNavigation.setMenuItemURL(url);
                this.formTestNavigation.setPrependHTMLTags(preprend);   
                
                this.formTestNavigation.createNewMenuItem();
            }
            
        } catch (ParserConfigurationException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "XML parse error", ex.getMessage());
        } catch (SAXException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "XML parse error", ex.getMessage());
        } catch (IOException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "File read error", ex.getMessage());
        }
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    
    
}
