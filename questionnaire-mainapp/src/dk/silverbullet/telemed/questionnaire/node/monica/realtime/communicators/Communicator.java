package dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators;

import android.content.Context;
import android.util.Log;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.node.monica.realtime.MilouSoapActions;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;

import java.io.UnsupportedEncodingException;

public abstract class Communicator {

    protected static final int MAX_RETRIES = 20;
    protected static final long RETRY_WAIT_PERIOD_IN_MILLISECONDS = 1500;
    protected static HttpClient httpClient;
    protected Context context;
    protected Questionnaire questionnaire;

    public Communicator(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        this.context = questionnaire.getContext();
        createHttpClient(context);
    }

    abstract String getTag();

    protected HttpEntity getHttpEntityForDocument(Document document, MilouSoapActions action) {
        HttpEntity entity = null;
        try {
            entity = new StringEntity(getStringFromDocument(document, action), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //If we ever run somewhere where UTF-8 isn't supported. Well..
        }
        return entity;
    }

    protected abstract String getStringFromDocument(Document document, MilouSoapActions action);

    protected abstract String getServerURL();

    public boolean sendMessagesDocument(Document document) {
        return processMessage(document, MilouSoapActions.NEW_MESSAGE);
    }

    public boolean sendStopMessage(Document document) {
        return processMessage(document, MilouSoapActions.STOP_REGISTRATION);
    }

    protected void createHttpClient(Context context) {
        httpClient = HttpClientFactory.createHttpClient(context);
        HttpParams httpParameters = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000);
    }

    protected HttpPost createHttpPostWithEntityForSoapAction(HttpEntity entity, MilouSoapActions action) {
        HttpPost result = new HttpPost(getServerURL());
        result.setEntity(entity);
        setHeaders(result, action);
        return result;
    }

    protected abstract void setHeaders(HttpPost post, MilouSoapActions action);

    protected boolean processMessage(Document document, MilouSoapActions action) {

        HttpPost httpPost = getHttpPostForDocumentAndAction(document, action);

        for(int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
            try {
                httpClient.execute(httpPost, new BasicResponseHandler());  //Will throw exception if response code > 300
                return true;
            } catch (Exception e) {
                Log.w(getTag(), "Network problems while sending measurements", e);
                OpenTeleApplication.instance().logException(e);
                pauseBeforeRetry();
            }
        }

        return false;
    }

    HttpPost getHttpPostForDocumentAndAction(Document document, MilouSoapActions action) {
        HttpEntity entity = getHttpEntityForDocument(document, action);
        return createHttpPostWithEntityForSoapAction(entity, action);
    }

    private void pauseBeforeRetry() {
        try {
            Thread.sleep(RETRY_WAIT_PERIOD_IN_MILLISECONDS);
        } catch (InterruptedException e1) {
            //ignored
        }
    }
}
