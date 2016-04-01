/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm;

import eds.component.data.RelationshipNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.component.landing.LandingService;
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionImplService;
import segmail.program.subscribe.confirm.client.WSConfirmSubscription;

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
    private LandingService landingService;

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
            //try {
            //QName qname = new QName("http://confirm.subscribe.program.seca2/", "WSConfirmSubscriptionImplService");
            //ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN);
            /*String urlText = ERP_SERVER_URL.concat("/WSConfirmSubscriptionImplService?wsdl");
            URL url = new URL(urlText);

            Service service = Service.create(url, qname);
            WSConfirmSubscription confirmWS = service.getPort(WSConfirmSubscription.class);

            Map<String, Object> req_ctx = ((BindingProvider) confirmWS).getRequestContext();
            req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlText);

            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            headers.put("Username", Collections.singletonList("sws"));
            headers.put("Password", Collections.singletonList("sws"));
            req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

            String result = confirmWS.confirm("fsfsgsrgwas");

            program.setResult(result);*/
            
            /*} catch (MalformedURLException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
            } catch (SOAPFaultException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getFault().getFaultString(), "");
            }*/
            
            WSConfirmSubscriptionImplService client = new WSConfirmSubscriptionImplService();
            WSConfirmSubscription clientService = client.getWSConfirmSubscriptionImplPort();
            String results = clientService.confirm(program.getRequestKey());
            
            this.setListName(results);
        } catch (RelationshipNotFoundException ex) {
            Logger.getLogger(FormConfirmSubcription.class.getName()).log(Level.SEVERE, null, ex);
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
