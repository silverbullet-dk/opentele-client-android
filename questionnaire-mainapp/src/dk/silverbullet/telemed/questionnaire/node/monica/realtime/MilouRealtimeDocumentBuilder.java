package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

import static dk.silverbullet.telemed.utils.Json.ISO8601_DATE_TIME_FORMAT;

public class MilouRealtimeDocumentBuilder {
    private static final String XMLNS_MIL = "http://schemas.datacontract.org/2004/07/Milou.Server.OpenTeleRT";
    private static final String XMLNS_ARR = "http://schemas.microsoft.com/2003/10/Serialization/Arrays";
    private static final String XMLNS_TEM = "http://tempuri.org/";
    private static final String XMLNS_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private final String deviceName;
    private final UUID registrationIdentifier;
    private PatientInfo patientInfo;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private ArrayList<RealTimeCTGMessage> messages;

    public MilouRealtimeDocumentBuilder(String deviceName, UUID registrationIdentifier, PatientInfo patientInfo) {
        this.deviceName = deviceName;
        this.registrationIdentifier = registrationIdentifier;
        this.patientInfo = patientInfo;

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void startNewMessagesDocument() {
        messages = new ArrayList<RealTimeCTGMessage>();
    }

    public void addMessageToDocument(RealTimeCTGMessage message) {
        messages.add(message);
    }

    public Document finishDocument() {
        return getDocumentForMessages(messages);
    }

    public Document buildStopDocument() {
        Document stopMessageDocument = documentBuilder.newDocument();
        Element soapEnvelope = createSoapEnvelope(stopMessageDocument);
        stopMessageDocument.appendChild(soapEnvelope);

        Element soapBody = (Element) soapEnvelope.getChildNodes().item(0);
        Element stopMessage = stopMessageDocument.createElementNS(XMLNS_TEM, "StopRegistration");
        soapBody.appendChild(stopMessage);

        Element deviceID = stopMessageDocument.createElementNS(XMLNS_TEM, "deviceID");
        deviceID.setTextContent(deviceName);
        stopMessage.appendChild(deviceID);

        Element registrationId = stopMessageDocument.createElementNS(XMLNS_TEM, "registrationID");  //Yes. It does have a different name in the StopRegistrationMessages
        registrationId.setTextContent(registrationIdentifier.toString());
        stopMessage.appendChild(registrationId);

        Element stopTime = stopMessageDocument.createElementNS(XMLNS_TEM, "stopTime");
        stopTime.setTextContent(calendarToXSDDateTime(Calendar.getInstance()));
        stopMessage.appendChild(stopTime);

        return stopMessageDocument;
    }

    private Document getDocumentForMessages(List<RealTimeCTGMessage> messages) {

        Document messagesDocument = documentBuilder.newDocument();
        Element soapEnvelope = createSoapEnvelope(messagesDocument);
        messagesDocument.appendChild(soapEnvelope);

        Element soapBody = (Element) soapEnvelope.getChildNodes().item(0);

        Element newDocumentElement = messagesDocument.createElementNS(XMLNS_TEM, "NewMessage");
        soapBody.appendChild(newDocumentElement);

        Element messageElement = messagesDocument.createElementNS(XMLNS_TEM, "message");
        newDocumentElement.appendChild(messageElement);

        Element ctgFieldMessage = messagesDocument.createElementNS(XMLNS_MIL, "ctgField");
        messageElement.appendChild(ctgFieldMessage);

        Element deviceIDFieldMessage = messagesDocument.createElementNS(XMLNS_MIL, "deviceIDField");
        deviceIDFieldMessage.setTextContent(deviceName);
        messageElement.appendChild(deviceIDFieldMessage);

        Element markersFieldMessage = messagesDocument.createElementNS(XMLNS_MIL, "markersField");
        messageElement.appendChild(markersFieldMessage);

        for(RealTimeCTGMessage message: messages) {
            handleMessage(message, messagesDocument, markersFieldMessage, ctgFieldMessage);
        }

        if(!markersFieldMessage.hasChildNodes()) {
            markersFieldMessage.setAttributeNS(XMLNS_XSI, "nil", "true");
        }

        addPatientFields(messageElement, messagesDocument);
        addRegistrationIDField(messageElement, messagesDocument, registrationIdentifier);

        return messagesDocument;
    }


    private void handleMessage(RealTimeCTGMessage message, Document document, Element markersElement, Element sampleSetElement) {
        if(message instanceof SignalMessage) {
            addSignalMessage((SignalMessage) message, document, markersElement);
        } else if(message instanceof SampleMessage) {
            addSampleMessage((SampleMessage) message, document, sampleSetElement);
        }
    }

    private Element createSoapEnvelope(Document document) {
        Element envelope = document.createElementNS(XMLNS_SOAP, "Envelope");
        Element body = document.createElementNS(XMLNS_SOAP, "Body");
        envelope.appendChild(body);

        return envelope;
    }

    private String calendarToXSDDateTime(Date date) {
        return ISO8601_DATE_TIME_FORMAT.format(date);
    }

    private String calendarToXSDDateTime(Calendar calendar) {
        return ISO8601_DATE_TIME_FORMAT.format(calendar.getTime());
    }

    private void addSampleMessage(SampleMessage message, Document document, Element parentElement) {

        Element sampleElement = document.createElementNS(XMLNS_MIL, "CtgMessageBlock");
        parentElement.appendChild(sampleElement);

        Element fhrField = document.createElementNS(XMLNS_MIL, "fhrField");
        fhrField.setTextContent(Arrays.toString(message.fhr));
        sampleElement.appendChild(fhrField);

        Element mhrField = document.createElementNS(XMLNS_MIL, "mhrField");
        mhrField.setTextContent(Arrays.toString(message.mhr));
        sampleElement.appendChild(mhrField);

        Element sequenceNbrField = document.createElementNS(XMLNS_MIL, "sequenceNbrField");
        sequenceNbrField.setTextContent(message.sampleCount + "");
        sampleElement.appendChild(sequenceNbrField);

        Element sqField = document.createElementNS(XMLNS_MIL, "sqField");
        sqField.setTextContent(Arrays.toString(message.qfhr));
        sampleElement.appendChild(sqField);

        Element timeField = document.createElementNS(XMLNS_MIL, "timeField");
        timeField.setTextContent(calendarToXSDDateTime(message.timeStamp));
        sampleElement.appendChild(timeField);

        Element tocoField = document.createElementNS(XMLNS_MIL, "tocoField");
        tocoField.setTextContent(Arrays.toString(message.toco));
        sampleElement.appendChild(tocoField);

    }

    private void addSignalMessage(SignalMessage message, Document document, Element parentElement) {
        Element dateTimeField = document.createElementNS(XMLNS_ARR, "dateTime");
        dateTimeField.setTextContent(calendarToXSDDateTime(message.dateTime));

        parentElement.appendChild(dateTimeField);
    }

    private void addPatientFields(Element parentElement, Document document) {

        Element patientElement = document.createElementNS(XMLNS_MIL, "patientField");

        Element idElement = document.createElementNS(XMLNS_MIL, "idField");
        idElement.setTextContent(patientInfo.id + "");
        patientElement.appendChild(idElement);

        Element nameElement = document.createElementNS(XMLNS_MIL, "nameField");
        patientElement.appendChild(nameElement);

        Element firstNameElement = document.createElementNS(XMLNS_MIL, "firstField");
        firstNameElement.setTextContent(patientInfo.firstName);
        nameElement.appendChild(firstNameElement);

        Element lastNameElement = document.createElementNS(XMLNS_MIL, "lastField");
        lastNameElement.setTextContent(patientInfo.lastName);
        nameElement.appendChild(lastNameElement);

        parentElement.appendChild(patientElement);
    }

    private void addRegistrationIDField(Element parentElement, Document document, UUID registrationIdentifier) {
        Element registrationField = document.createElementNS(XMLNS_MIL, "registrationIDField");
        registrationField.setTextContent(registrationIdentifier.toString());

        parentElement.appendChild(registrationField);
    }
}
