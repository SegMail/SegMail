/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm;

import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.chartjs.ChartJSServiceImpl;
import seca2.program.chartjs.ChartJSService;
import segmail.component.landing.LandingService;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormConfirmSubcription")
public class FormConfirmSubcription {
    
    private final String WEB_SERVICE_ENDPOINT = "/SegMail/ChartJSService";
    private final String WEB_SERVICE_METHOD = "outstandingLoanOverTime";
    private final String WEB_SERVICE_NAMESPACE_QUALIFIER = "ns2";
    private final String WEB_SERVICE_QNAME = "http://chartjs.program.seca2/";
    private final String WEB_SERVICE_LOCAL_PART = "ChartJSService";
    
    //Hardcoded
    private final String ERP_SERVER_URL = "http://localhost:8080/SegMail";
    //private final String ERP_SERVER_URL = "http://lees-macbook-pro.local:28081/SegMailERP";
    
    @EJB private LandingService landingService;
    
    @Inject private ProgramConfirmSubscription program;
    
    @PostConstruct
    public void init(){
        callWS();
    }
    
    public void callWS(){
        
        try {
            QName qname = new QName("http://chartjs.program.seca2/","ChartJSServiceImplService");
            //ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN);
            String urlText = ERP_SERVER_URL.concat("/ChartJSServiceImplService?wsdl");
            URL url = new URL(urlText);
            
            Service service = Service.create(url,qname);
            ChartJSService chartJS = service.getPort(ChartJSService.class);
            
            int result = chartJS.add(1, 1);
            
            program.setResult(result);
        } catch (MalformedURLException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (Exception ex) {
            System.out.println("Web service error: "+ex.getMessage());
        }
        
        
    }
    
    public int getResult() {
        return program.getResult();
    }

    public void setResult(int result) {
        program.setResult(result);
    }
    
}
