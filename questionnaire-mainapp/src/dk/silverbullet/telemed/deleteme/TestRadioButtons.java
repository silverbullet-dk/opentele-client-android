package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.RadioButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.ValueChoice;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestRadioButtons implements TestSkema {

    private static final String TAG = Util.getTag(TestRadioButtons.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {

        OutputSkema outputSkema = new OutputSkema();
        Variable<Integer> selection = new Variable<Integer>("selection", Integer.class);

        outputSkema.addVariable(selection);

        EndNode end = new EndNode(questionnaire, "End");

        IONode ionode = new IONode(questionnaire, "radio");
        TextViewElement tve = new TextViewElement(ionode, "Hvor meget slim har du i lungerne?");
        tve.setHeader(true);
        ionode.addElement(tve);

        @SuppressWarnings("unchecked")
        // Arrays and generics don't mix! :-/
        ValueChoice<Integer>[] values = new ValueChoice[6];

        values[0] = new ValueChoice<Integer>(5, "5: Mine lunger er helt fyldte med slim");
        values[1] = new ValueChoice<Integer>(4, "4");
        values[2] = new ValueChoice<Integer>(3, "3");
        values[3] = new ValueChoice<Integer>(2, "2");
        values[4] = new ValueChoice<Integer>(1, "1");
        values[5] = new ValueChoice<Integer>(0, "0: Der er slet ikke noget slim i mine lunger");

        RadioButtonElement<Integer> radio = new RadioButtonElement<Integer>(ionode);
        radio.setOutputVariable(selection);
        radio.setChoices(values);

        ionode.addElement(radio);

        ButtonElement be = new ButtonElement(ionode);
        be.setText("Fortsæt");
        be.setGravity(ButtonElement.GRAVITY_CENTER);
        be.setNext(end.getNodeName());
        ionode.addElement(be);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("Mini");
        skema.setStartNode(ionode.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(ionode);

        skema.link();

        return skema;
    }

    @Override
    public Skema getSkema() {
        Questionnaire q = new Questionnaire(new QuestionnaireFragment());
        try {
            String json = Json.print(getInternSkema(q));
            return Json.parse(json, Skema.class);
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got exception", e);
        }
        return null;
    }
}
