package dk.silverbullet.telemed.questionnaire.node;

import android.app.Activity;
import android.util.Log;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RealtimeCTGMeasurementsExportWorker extends Thread {
    private static final String TAG = Util.getTag(RealtimeCTGMeasurementsExportWorker.class);
    private static final int MAX_RETRIES = 3;
    private static final int BATCH_SIZE = 10;
    private static HttpClient httpClient;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Activity activity;
    private BlockingQueue<RealTimeCTGMessage> measurementsQueue;
    private PatientInfo patientInfo;



    public RealtimeCTGMeasurementsExportWorker(Activity activity, BlockingQueue<RealTimeCTGMessage> measurementsQueue, PatientInfo patientInfo) {
        this.activity = activity;
        this.measurementsQueue = measurementsQueue;
        this.patientInfo = patientInfo;

        createHttpClient();

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while(true) {
            pullMessages();
        }
    }

    private void pullMessages() {
        try {
            Log.d(TAG, "QUEUE length:" + measurementsQueue.size());
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("reg");
            document.appendChild(rootElement);

            addPatientFields(rootElement, document);
            take10FromQueueAndAddElements(document, rootElement);

            sendMessageToServer(document);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void take10FromQueueAndAddElements(Document document, Element rootElement) throws InterruptedException {
        Element sampleSetElement = document.createElement("SampleSet");
        Element signalsElement = document.createElement("Signals");

        List<RealTimeCTGMessage> messageBatch = takeTen();

        for(RealTimeCTGMessage message: messageBatch) {
            handleMessage(message, document, signalsElement, sampleSetElement);
        }
        rootElement.appendChild(sampleSetElement);
        rootElement.appendChild(signalsElement);
    }

    private List<RealTimeCTGMessage> takeTen() throws InterruptedException {
        List<RealTimeCTGMessage> messageBatch = new ArrayList<RealTimeCTGMessage>(BATCH_SIZE);

        messageBatch.add(measurementsQueue.take()); //We know we have at least one message, since take is blocking
        measurementsQueue.drainTo(messageBatch, BATCH_SIZE - 1); //We have already added one elment above
        return messageBatch;
    }

    private void handleMessage(RealTimeCTGMessage message, Document document, Element signalsElement, Element sampleSetElement) {
        if(message instanceof SignalMessage) {
            addSignalMessage((SignalMessage) message, document, signalsElement);
        } else {
            addSampleMessage((SampleMessage) message, document, sampleSetElement);
        }
    }

    private void addSampleMessage(SampleMessage message, Document document, Element parentElement) {
        Element sampleElement = document.createElement("sample");
        sampleElement.setAttribute("timestamp", message.timeStamp.getTime().toString());
        sampleElement.setAttribute("sequenceNumber", message.sampleCount + "");

        Element tocoElement = document.createElement("toco");
        tocoElement.setTextContent(Arrays.toString(message.toco));
        sampleElement.appendChild(tocoElement);

        Element fhrElement = document.createElement("fhr");
        fhrElement.setTextContent(Arrays.toString(message.fhr));
        sampleElement.appendChild(fhrElement);

        Element mhrElement = document.createElement("mhr");
        mhrElement.setTextContent(Arrays.toString(message.mhr));
        sampleElement.appendChild(mhrElement);

        Element qfhrElement = document.createElement("qfhr");
        qfhrElement.setTextContent(Arrays.toString(message.qfhr));
        sampleElement.appendChild(qfhrElement);

        parentElement.appendChild(sampleElement);

    }

    private void addSignalMessage(SignalMessage message, Document document, Element parentElement) {
        Element signalElement = document.createElement("dateTime");
        signalElement.setTextContent(message.dateTime.toString());
        parentElement.appendChild(signalElement);
    }

    private void sendMessageToServer(Document document) {
        HttpEntity entity = null;
        try {
            entity = new StringEntity(getStringFromDocument(document), "UTF-8");
        } catch (UnsupportedEncodingException e) {}


        HttpPost httpPost = createHttpPostForPathWithEntity(entity);

        for(int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
            Log.d(TAG, "Retry count:" + retryCount);
            try {
                httpClient.execute(httpPost);
                break; //message sent successfully do not retry
            } catch (Exception e) {
                Log.w(TAG, "Network problems while sending measurements", e);
                e.printStackTrace();
            }
        }
    }

    private String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "<serializationError/>";
    }

    private void addPatientFields(Element parentElement, Document document) {

        Element patientElement = document.createElement("Patient");

        Element idElement = document.createElement("id");
        idElement.setTextContent(patientInfo.id + "");
        patientElement.appendChild(idElement);

        Element nameElement = document.createElement("Name");
        Element firstNameElement = document.createElement("firstName");
        firstNameElement.setTextContent(patientInfo.firstName);
        nameElement.appendChild(firstNameElement);
        Element lastNameElement = document.createElement("lastName");
        lastNameElement.setTextContent(patientInfo.lastName);
        nameElement.appendChild(lastNameElement);
        patientElement.appendChild(nameElement);

        parentElement.appendChild(patientElement);
    }

    private void createHttpClient() {
        httpClient = HttpClientFactory.createHttpClient(activity);
        HttpParams httpParameters = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000);
    }

    private HttpPost createHttpPostForPathWithEntity(HttpEntity entity) {
        HttpPost result = new HttpPost("http://10.0.1.14:4567");
        result.setEntity(entity);
        result.addHeader("Content-Type", "application/xml");
        return result;
    }

}
