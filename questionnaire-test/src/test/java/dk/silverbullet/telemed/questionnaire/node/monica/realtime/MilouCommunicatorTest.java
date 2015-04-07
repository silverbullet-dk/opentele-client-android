package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import android.content.Context;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators.MilouCommunicator;
import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class MilouCommunicatorTest {

    private MilouCommunicator communicator;

    private HttpPost mockHttpPost;
    private HttpClient mockHttpClient;
    private OpenTeleApplication mockApplication;

    @Before
    public void setup() {
        mockHttpClient = mock(HttpClient.class);
        mockHttpPost = mock(HttpPost.class);
        mockApplication = mock(OpenTeleApplication.class);
        communicator = new TestMilouCommunicator(new Questionnaire(mock(QuestionnaireFragment.class)), mockApplication);
    }

    @Test
    public void httpExceptionsCausesRetries() throws IOException {
        Exception exception = new HttpResponseException(400, "ERROR");

        when(mockHttpClient.execute(any(HttpPost.class), any(ResponseHandler.class))).thenThrow(exception);

        boolean success = communicator.sendMessagesDocument(BuildDocument());
        Assert.assertFalse(success);
        // Verify retries
        verify(mockHttpClient, times(5)).execute(any(HttpPost.class), any(ResponseHandler.class));
    }

    @Test
    public void nonHttpExceptionsWillCauseRetries() throws IOException {
        Exception exception = new IOException("ERROR");

        when(mockHttpClient.execute(any(HttpPost.class), any(ResponseHandler.class))).thenThrow(exception);

        boolean success = communicator.sendMessagesDocument(BuildDocument());
        Assert.assertFalse(success);
        // Verify retries
        verify(mockHttpClient, times(5)).execute(any(HttpPost.class), any(ResponseHandler.class));
    }

    private Document BuildDocument() {
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.firstName = "NancyAnn";
        patientInfo.lastName = "Berggren";
        patientInfo.id = 1l;

        MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder("unit-test-device", UUID.randomUUID(), patientInfo);

        SignalMessage signalMessage = new SignalMessage(Calendar.getInstance().getTime());
        builder.startNewMessagesDocument();
        builder.addMessageToDocument(signalMessage);
        return builder.finishDocument();
    }

    private class TestMilouCommunicator extends MilouCommunicator {

        public TestMilouCommunicator(Questionnaire questionnaire, OpenTeleApplication openTeleApplication) {
            super(questionnaire, openTeleApplication);
            setMaxRetries(5);
            setRetryWaitPeriodInMilliseconds(1);
        }

        @Override
        protected void createHttpClient(Context context) {
            httpClient = mockHttpClient;
        }

        @Override
        protected HttpPost createHttpPostWithEntityForSoapAction(HttpEntity entity, MilouSoapActions action) {
            return mockHttpPost;
        }
    }
}