/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm;

import java.net.URL;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;
import seca2.bootstrap.UserRequestContainer;
import seca2.component.landing.LandingService;
import eds.component.webservice.GenericWSProvider;
import eds.component.webservice.WebserviceService;
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;

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
    
    @Inject UserRequestContainer reqContainer;
    
    @EJB
    private WebserviceService wsService;

    @Inject
    private ProgramConfirmSubscription program;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if(!fc.isPostback()){
            extractParams(reqContainer);
            callWS();
        }
        
    }

    public void callWS() {

        try {
            //URL wsdlLocaton = new URL("http://localhost:28081/SegMailERP/WSConfirmSubscription?wsdl");
            //QName serviceName = new QName("http://webservice.confirm.subscribe.program.segmail/", "WSConfirmSubscription");
            //QName portName = new QName("http://webservice.confirm.subscribe.program.segmail/", 
            //            "WSConfirmSubscriptionPort");
            String namespace = "http://webservice.confirm.subscribe.program.segmail/";
            String endpointName = "WSConfirmSubscription";
            WSConfirmSubscriptionInterface clientService = wsService.getWSProvider(endpointName, namespace, WSConfirmSubscriptionInterface.class);
            String results = clientService.confirm("Test");
            
            this.setListName(results);
            
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public String getListName() {
        return program.getListName();
    }

    public void setListName(String result) {
        program.setListName(result);
    }

    public void extractParams(UserRequestContainer reqContainer) {
        List<String> params = reqContainer.getProgramParamsOrdered();
        if(params == null || params.isEmpty())
            return;
        
        String reqKey = params.get(0);
        program.setRequestKey(reqKey);
    }
}
