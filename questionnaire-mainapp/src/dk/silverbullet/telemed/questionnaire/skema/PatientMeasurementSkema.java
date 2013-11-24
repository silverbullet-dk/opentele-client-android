package dk.silverbullet.telemed.questionnaire.skema;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.node.WebViewNode;
import dk.silverbullet.telemed.utils.Util;

public class PatientMeasurementSkema implements SkemaDef {
    private static final String TAG = Util.getTag(PatientMeasurementSkema.class);

    @Override
    public Skema getSkema(Questionnaire questionnaire) {

        EndNode end = new EndNode(questionnaire, "End");
        String patientMeasurementsURL = Util.getServerUrl(questionnaire) + "rest/patient/measurements";
        WebViewNode webViewNode = new WebViewNode(questionnaire, "patientMeasurementNode", patientMeasurementsURL, Util.getString(R.string.patient_measurements_my_measurements, questionnaire));

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("PATIENT_MEASUREMENTS");
        skema.setStartNode(webViewNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(webViewNode);

        try {
            skema.link();
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got Exception", e);
        }

        return skema;
    }

}
