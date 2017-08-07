/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder.webservice;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.joda.time.DateTime;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSAutoresponder")
@HandlerChain(file = "handlers-server.xml")
public class WSAutoresponder {

    @EJB
    AutoresponderService autoemailService;
    @EJB
    MailMergeService mmService;
    @EJB
    private GenericObjectService objectService;

    //@Inject ProgramAutoresponder program;
    @Inject
    AutoresponderSessionContainer listCont;

    /**
     * Saves the body and bodyProcessed into AutoresponderEmail BODY and
     * BODY_PROCESSED fields for the existing editing AutoresponderEmail that is
     * in context.
     *
     * @param subject
     * @param body
     * @param bodyProcessed
     * @return
     * @throws eds.component.data.EntityNotFoundException
     * @throws eds.component.data.IncompleteDataException
     * @throws eds.component.data.EntityExistsException
     */
    @WebMethod(operationName = "saveAutoemail")
    public String saveAutoemail(
            @WebParam(name = "subject") String subject,
            @WebParam(name = "body") String body,
            @WebParam(name = "bodyProcessed") String bodyProcessed)
            throws EntityNotFoundException, IncompleteDataException, EntityExistsException, DataValidationException {

        AutoresponderEmail autoemail = listCont.getEditingTemplate();

        if (autoemail == null) {
            throw new EntityNotFoundException("No AutoresponderEmail found.");
        }

        autoemail.setSUBJECT(subject);
        autoemail.setBODY(body);
        //autoemail.setBODY_PROCESSED(bodyProcessed);

        autoemail = autoemailService.saveAutoEmail(autoemail);
        listCont.setEditingTemplate(autoemail);

        DateTime now = DateTime.now();

        return now.toString("dd-MM-yyyy hh:mm:ss");

    }

    /**
     * Retrieves a representation of a clickable link
     *
     * @param label
     * @return JSON object that encompasses a name and a URL
     * @throws DataValidationException if the input label is not recognized by
     * MAILMERGE_REQUEST
     * @throws IncompleteDataException if the test server is not set up
     */
    @WebMethod(operationName = "createSystemMailmergeTestLink")
    public String createSystemMailmergeTestLink(@WebParam(name = "label") String label)
            throws DataValidationException, IncompleteDataException {

        String url = mmService.getSystemTestLink(label);

        JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
        resultObjectBuilder.add("name", MAILMERGE_REQUEST.getByLabel(label).toCapFirstLetter());
        resultObjectBuilder.add("url", url);

        String result = resultObjectBuilder.build().toString();

        return result;
    }

    /**
     *
     * @param label
     * @return the field value of a random subscriber from the assigned list
     */
    @WebMethod(operationName = "createSubscriberMailmergeTestValue")
    public String createSubscriberMailmergeTestValue(@WebParam(name = "label") String label) {
        List<SubscriptionListField> fields = listCont.getFields();

        for (SubscriptionListField field : fields) {
            if (field.getMAILMERGE_TAG().equals(label)) {
                String value = listCont.getRandomSubscriber().get(field.generateKey());
                return (value == null) ? label : value;
            }
        }

        return label;
    }
}
