package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


/*
 * If you're having trouble running these tests from your IDE try running 'mvn test' from the commandline first
 */
public class MilouRealtimeDocumentBuilderTest {

    PatientInfo patientInfo;
    UUID registratonId;
    String deviceName;

    File newMessageSchema = fileFor("schemas/newMessageSchema.xsd");

    @Before
    public void setupPatientInfo() {
        this.patientInfo = new PatientInfo();

        patientInfo.firstName = "NancyAnn";
        patientInfo.lastName = "Berggren";
        patientInfo.id = 1l;
    }

    @Before
    public void setupRegistrationId() {
        this.registratonId = UUID.randomUUID();
    }

    @Before
    public void setupDeviceName() {
        this.deviceName = "UnitTest-Device";
    }

    @Test
    public void canBuildDocumentFromSingleSignalMessage() {
        MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder(deviceName, registratonId, patientInfo);

        SignalMessage signalMessage = new SignalMessage(Calendar.getInstance().getTime());
        builder.startNewMessagesDocument();
        builder.addMessageToDocument(signalMessage);
        Document document = builder.finishDocument();

        Assert.assertTrue(soapBodyContentsAreSchemaValid(document));
    }



    @Test
    public void canBuildDocumentFromSingleSampleMessage() {
         MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder(deviceName, registratonId, patientInfo);

         float[] mhr = {1f, 2f, 3f, 4f};
         float[] fhr = {5f, 6f, 7f, 8f};
         int[] qfhr = {9, 10, 11, 12};
         float[] toco = {13f, 14f, 15f, 16f};

         SampleMessage sampleMessage = new SampleMessage(mhr, fhr, qfhr, toco, 0);
         builder.startNewMessagesDocument();
         builder.addMessageToDocument(sampleMessage);
         Document document = builder.finishDocument();

         System.out.println(documentToString(document));

         Assert.assertTrue(soapBodyContentsAreSchemaValid(document));
    }

    @Test
    public void canBuildDocumentFromMultipleSignalAndSampleMessages() {
        MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder(deviceName, registratonId, patientInfo);

        float[] mhr = {1f, 2f, 3f, 4f};
        float[] fhr = {5f, 6f, 7f, 8f};
        int[] qfhr = {9, 10, 11, 12};
        float[] toco = {13f, 14f, 15f, 16f};

        List<RealTimeCTGMessage> messages = new ArrayList<RealTimeCTGMessage>();
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SignalMessage(Calendar.getInstance().getTime()));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SignalMessage(Calendar.getInstance().getTime()));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SignalMessage(Calendar.getInstance().getTime()));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SignalMessage(Calendar.getInstance().getTime()));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SampleMessage(mhr, fhr, qfhr, toco, 0));
        messages.add(new SignalMessage(Calendar.getInstance().getTime()));

        builder.startNewMessagesDocument();

        for(RealTimeCTGMessage message: messages) {
            builder.addMessageToDocument(message);
        }

        Document document = builder.finishDocument();

        System.out.println(documentToString(document));

        Assert.assertTrue(soapBodyContentsAreSchemaValid(document));
    }

    @Test
    public void canBuildStopMessage() {
        MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder(deviceName, registratonId, patientInfo);
        Document document = builder.buildStopDocument();

        Assert.assertTrue(soapBodyContentsAreSchemaValid(document));
    }

    @Test
    public void addsRegistrationIDToNewMessageDocument() {
        MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder(deviceName, registratonId, patientInfo);

        SignalMessage signalMessage = new SignalMessage(Calendar.getInstance().getTime());
        builder.startNewMessagesDocument();
        builder.addMessageToDocument(signalMessage);
        Document document = builder.finishDocument();

        Assert.assertTrue(soapBodyContentsAreSchemaValid(document));

        assertRegistrationID(document, "registrationIDField");
    }


    @Test
    public void addsRegistrationIDToNStopRegistrationMessageDocument() {
        MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder(deviceName, registratonId, patientInfo);
        Document document = builder.buildStopDocument();

        Assert.assertTrue(soapBodyContentsAreSchemaValid(document));
        assertRegistrationID(document, "registrationID");

    }

    @Test
    public void addsPatientInfoNewMessageDocument() {
        MilouRealtimeDocumentBuilder builder = new MilouRealtimeDocumentBuilder(deviceName, registratonId, patientInfo);

        float[] mhr = {1f, 2f, 3f, 4f};
        float[] fhr = {5f, 6f, 7f, 8f};
        int[] qfhr = {9, 10, 11, 12};
        float[] toco = {13f, 14f, 15f, 16f};

        SampleMessage sampleMessage = new SampleMessage(mhr, fhr, qfhr, toco, 0);
        builder.startNewMessagesDocument();
        builder.addMessageToDocument(sampleMessage);
        Document document = builder.finishDocument();

        Assert.assertTrue(soapBodyContentsAreSchemaValid(document));

        NodeList registratonIdNodes = document.getElementsByTagName("patientField");
        Assert.assertEquals(1, registratonIdNodes.getLength());

        NodeList idFieldNodes = document.getElementsByTagName("idField");
        Assert.assertEquals(1, idFieldNodes.getLength());
        Assert.assertEquals(patientInfo.id + "", idFieldNodes.item(0).getTextContent());

        NodeList firstFieldNodes = document.getElementsByTagName("firstField");
        Assert.assertEquals(1, firstFieldNodes.getLength());
        Assert.assertEquals(patientInfo.firstName + "", firstFieldNodes.item(0).getTextContent());

        NodeList lastFieldNodes = document.getElementsByTagName("lastField");
        Assert.assertEquals(1, lastFieldNodes.getLength());
        Assert.assertEquals(patientInfo.lastName + "", lastFieldNodes.item(0).getTextContent());

    }

    private void assertRegistrationID(Document document, String registrationFieldName) {
        NodeList registratonIdNodes = document.getElementsByTagName(registrationFieldName);
        Assert.assertEquals(1, registratonIdNodes.getLength());
        Assert.assertEquals(registratonId.toString(), registratonIdNodes.item(0).getTextContent());
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
            e.printStackTrace();
        }
        return "<serializationError/>";
    }


    private boolean soapBodyContentsAreSchemaValid(Document soapEnvelopeDocument) {
        Document bodyContents = documentFromSoapBodyContents(soapEnvelopeDocument);
        try {
            DocumentBuilder builder = null;
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(newMessageSchema);

            Validator validator = schema.newValidator();
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw new RuntimeException("Validation error", exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw new RuntimeException("Validation error", exception);
                }
            });

            DocumentBuilderFactory domParserFactory = DocumentBuilderFactory.newInstance();
            domParserFactory.setValidating(true);
            domParserFactory.setSchema(schema);

            SAXSource source = new SAXSource(new InputSource(new StringReader(documentToString(bodyContents))));
            validator.validate(source);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static File fileFor(String resource) {
        try {
            return new File(MilouRealtimeDocumentBuilderTest.class.getResource(resource).toURI());
        } catch (URISyntaxException e) {
//            Should never, ever be able to happen
            throw new IllegalStateException("class.getResource(...) gives an invalid URI? I don't think so.", e);
        }
    }

    private Document documentFromSoapBodyContents(Document document) {
        try {
                                 //Envelope      //Body          //Body contents
            Node node = document.getFirstChild().getFirstChild().getFirstChild();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = null;

            builder = factory.newDocumentBuilder();

            Document newDocument = builder.newDocument();
            Node importedNode = newDocument.importNode(node, true);
            newDocument.appendChild(importedNode);

            return newDocument;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
