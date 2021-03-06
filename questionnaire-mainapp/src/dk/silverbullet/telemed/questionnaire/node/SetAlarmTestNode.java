package dk.silverbullet.telemed.questionnaire.node;

import android.content.Context;
import dk.silverbullet.telemed.deleteme.TestBloodSugar;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.bean.ReminderBean;
import dk.silverbullet.telemed.schedule.ReminderService;
import dk.silverbullet.telemed.utils.Util;

import java.util.Arrays;
import java.util.Map;

public class SetAlarmTestNode extends IONode {

    private Variable<String> serverIP;
    private Node nextNode;

    public SetAlarmTestNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        setView();
        super.enter();

        Context context = getQuestionnaire().getContext().getApplicationContext();

        ReminderBean reminder = new ReminderBean();
        reminder.setQuestionnaireName(new TestBloodSugar().getSkema().getName());
        reminder.setAlarms(Arrays.asList(10L)); // Alarm hits in 10 seconds

        ReminderService.setRemindersTo(context, reminder);

        Util.showToast(getQuestionnaire(), Util.getString(R.string.set_alarm_alarm_set, questionnaire));
        getQuestionnaire().setCurrentNode(nextNode);
    }

    private void setView() {
        clearElements();

        TextViewElement tve = new TextViewElement(this);
        tve.setText(Util.getString(R.string.set_alarm_server_ip, questionnaire));
        addElement(tve);

        EditTextElement ete = new EditTextElement(this);
        ete.setOutputVariable(serverIP);
        addElement(ete);

        ButtonElement be = new ButtonElement(this);
        be.setNextNode(nextNode);
        be.setText(Util.getString(R.string.default_ok, questionnaire));
        addElement(be);
    }

    @Override
    public void leave() {
        super.leave();
        Util.saveVariables(questionnaire);
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        serverIP = Util.linkVariable(map, serverIP);

        super.linkVariables(map);
    }

    @Override
    public String toString() {
        return "SetServerIpNode";
    }

    public void setServerIP(Variable<String> serverIP) {
        this.serverIP = serverIP;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
