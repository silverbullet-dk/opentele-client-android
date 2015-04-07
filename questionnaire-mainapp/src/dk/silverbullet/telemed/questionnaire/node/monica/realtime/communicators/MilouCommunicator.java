package dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators;

import android.util.Log;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.node.monica.realtime.MilouSoapActions;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.client.methods.HttpPost;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class MilouCommunicator extends Communicator {

    public MilouCommunicator(Questionnaire questionnaire, OpenTeleApplication openTeleApplication) {
        super(questionnaire, openTeleApplication);
    }

    @Override
    protected String getServerURL() {
        return Util.getString(R.string.milou_realtime_server_url, context);
    }

    @Override
    protected void setHeaders(HttpPost post, MilouSoapActions action) {
        post.addHeader("Content-Type", "text/xml");  //Using application/xml causes internal server-error from the milou server
        post.addHeader("SOAPAction", action.getActionString());
    }

    @Override
    protected boolean shouldRetry(Exception exception) {
        return true;
    }

    protected String getStringFromDocument(Document doc, MilouSoapActions action) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {
            Log.e(getTag(), "Could not serialize document for milou", e);
        }
        return "<serializationError/>";
    }

    @Override
    String getTag() {
        return Util.getTag(MilouCommunicator.class);
    }
}
