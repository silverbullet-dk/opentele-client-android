package dk.silverbullet.telemed.questionnaire.node;

import android.app.ProgressDialog;
import android.util.Log;
import com.google.gson.Gson;
import dk.silverbullet.telemed.deleteme.*;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ListViewElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.rest.RetrieveQuestionnaireListTask;
import dk.silverbullet.telemed.rest.RetrieveTask;
import dk.silverbullet.telemed.rest.bean.ListBean;
import dk.silverbullet.telemed.rest.listener.ListListener;
import dk.silverbullet.telemed.schedule.ReminderService;
import dk.silverbullet.telemed.schedule.bean.QuestionnaireSchedule;
import dk.silverbullet.telemed.utils.Util;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IOSkemaMenuNode extends IONode implements ListListener {

    private static final String TAG = Util.getTag(IOSkemaMenuNode.class);

    private Node nextNode;
    private Variable<String> skemaName;

    private Map<String, String> skemaer = new LinkedHashMap<String, String>();

    private Set<String> res = new HashSet<String>();

    private ProgressDialog dialog;

    public IOSkemaMenuNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);

        Variable<?> user = questionnaire.getValuePool().get(Util.VARIABLE_USERNAME);
        if (null != user && null != user.getExpressionValue() && null != user.getExpressionValue().toString()
                && Util.ADMINUSER_NAME.equalsIgnoreCase(user.getExpressionValue().toString())) {

            skemaer.put(new TestBloodPressure().getSkema().getName(), TestBloodPressure.class.getName());
            skemaer.put("Lokal :: Jordemoder spørgetræ-200611", TestJordemoder.class.getName());
            skemaer.put("Lokal :: Lungefunktion", TestLungMonitor.class.getName());
            skemaer.put("Lokal :: Mini", TestSimple.class.getName());
            skemaer.put("Lokal :: Monica, enter parameters", MonicaSkemaLimited.class.getName());
            skemaer.put("Lokal :: Monica user limited", MonicaSkemaUserLimited.class.getName());
            skemaer.put("Lokal :: Piratos test", TestPiratos.class.getName());
            skemaer.put("Lokal :: Saturation", TestSaturation.class.getName());
            skemaer.put("Lokal :: Blodsukker", TestBloodSugar.class.getName());
            skemaer.put("Lokal :: Temperature", TestTemperature.class.getName());
            skemaer.put("Lokal :: TestRadioButtons", TestRadioButtons.class.getName());
            skemaer.put("Lokal :: Urin", TestUrine.class.getName());
            skemaer.put("Lokal :: Delay", TestDelayNode.class.getName());
        }
    }

    @Override
    public void enter() {
        Log.d(TAG, "enter....");
        hideBackButton();

        dialog = ProgressDialog.show(questionnaire.getActivity(), "Henter skemaer", "Vent venligst...", true);

        RetrieveTask asyncHttpPost = new RetrieveQuestionnaireListTask(questionnaire, this);
        asyncHttpPost.execute(TestOutputSkema.getOutputSkema());

        buildView();
        super.enter();
    }

    @Override
    public void leave() {
        dialog.dismiss();
    }

    @Override
    public String toString() {
        return "IOSkemaMenuNode(\"" + getNodeName() + "\") -> \"" + nextNode.getNodeName() + "\"";
    }

    @Override
    public void setJson(String json) {
        Gson gson = new Gson();
        ListBean listBean = gson.fromJson(json, ListBean.class);

        if (null != listBean) {
            for (QuestionnaireSchedule schedule : listBean.getQuestionnaires()) {
                skemaer.put(schedule.getSkemaName(), gson.toJson(schedule));
            }
        }

        buildView();
        createView();
        dialog.dismiss();
    }

    @Override
    public void sendError() {
        dialog.dismiss();
    }

    private void buildView() {
        clearElements();

        TextViewElement tve = new TextViewElement(this);
        tve.setText("Vælg et spørgeskema");
        addElement(tve);

        ListViewElement<String> lve = new ListViewElement<String>(this);
        String[] vals = skemaer.keySet().toArray(new String[skemaer.size()]);
        lve.setValues(vals);
        String[] res = new String[skemaer.size()];
        int i = 0;
        for (String key : vals) {
            String skema = skemaer.get(key);
            Log.d(TAG, key + " skema: " + skema);
            res[i++] = skema;
        }

        lve.setResults(res);
        lve.setValuesToHighlight(questionnaireNamesToHighlight(skemaer));
        lve.setVariable(skemaName); // TODO Check this!!!!
        lve.setNextNode(nextNode);

        addElement(lve);
    }

    private String[] questionnaireNamesToHighlight(Map<String, String> questionnaires) {
        Set<String> result = new HashSet<String>();

        for (String fullQuestionnaireName : questionnaires.keySet()) {
            String questionnaireJson = questionnaires.get(fullQuestionnaireName);
            String questionnaireName = questionnaireNameFromQuestionnaireJson(questionnaireJson);
            if (ReminderService.shouldHightlightQuestionnaire(questionnaireName)) {
                result.add(fullQuestionnaireName);
            }
        }

        return result.toArray(new String[0]);
    }

    private String questionnaireNameFromQuestionnaireJson(String questionnaireJson) {
        // This is to make admin/admin work.
        if (null == questionnaireJson || questionnaireJson.startsWith("dk.silverbulle"))
            for (String key : skemaer.keySet()) {
                String val = skemaer.get(key);
                if (val.equals(questionnaireJson))
                    return key;
            }
        try {
            return new JSONObject(questionnaireJson).getString("name");
        } catch (JSONException e) {
            throw new IllegalArgumentException("Not proper JSON: '" + questionnaireJson + "'", e);
        }
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setSkemaName(Variable<String> skemaName) {
        this.skemaName = skemaName;
    }
}
