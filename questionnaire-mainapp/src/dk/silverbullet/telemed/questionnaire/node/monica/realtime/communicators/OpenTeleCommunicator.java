package dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators;

import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.node.monica.realtime.MilouSoapActions;
import dk.silverbullet.telemed.rest.client.lowlevel.HttpHeaderBuilder;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class OpenTeleCommunicator extends Communicator {

    public OpenTeleCommunicator(Questionnaire questionnaire, OpenTeleApplication openTeleApplication) {
        super(questionnaire, openTeleApplication);
    }

    @Override
    String getTag() {
        return Util.getTag(OpenTeleCommunicator.class);
    }

    @Override
    protected String getStringFromDocument(Document document, MilouSoapActions action) {
        try {
            OpenTeleRealtimeCTG registration = new OpenTeleRealtimeCTG();
            registration.soapAction = action.getActionString();
            registration.xml = Base64.encodeToString(documentToString(document).getBytes("UTF-8"), Base64.DEFAULT);

            return new Gson().toJson(registration);

        } catch (UnsupportedEncodingException e) {
            openTeleApplication.logException(e);
            Log.e(getTag(), "Could not serialize document for OpenTele", e);
        }
        openTeleApplication.logMessage("Could not serialize xml document");
        return "<serializationError/>";
    }

    @Override
    protected String getServerURL() {
        return Util.getServerUrl(questionnaire) + "rest/realTimeCTG/save";
    }

    @Override
    protected void setHeaders(HttpPost post, MilouSoapActions action) {
        new HttpHeaderBuilder(post, questionnaire)
                .withAuthentication()
                .withAcceptTypeJSON()
                .withContentTypeJSON();
    }

    @Override
    protected boolean shouldRetry(Exception exception) {
        HttpResponseException httpError = (exception instanceof HttpResponseException ? (HttpResponseException)exception : null);
        if (httpError != null && httpError.getStatusCode() == 429) {
            return false;
        }

        return true;
    }


    private String documentToString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {

            openTeleApplication.logException(e);
            Log.e(getTag(), "Could not serialize document for OpenTele", e);
        }
        openTeleApplication.logMessage("Could not serialize xml document");
        return "<serializationError/>";
    }
}
