package dk.silverbullet.telemed.deleteme;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.RadioButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.ValueChoice;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.MonicaDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.SaveFileNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class MonicaSkemaLimited implements TestSkema {

    @Override
    public Skema getSkema() throws UnknownNodeException {
        Questionnaire q = new Questionnaire(new QuestionnaireFragment());
        String json = Json.print(getInternSkema(q));
        return Json.parse(json, Skema.class);
    }

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {

        Variable<String> deviceId = new Variable<String>("DeviceID", String.class);
        Variable<Boolean> simulated = new Variable<Boolean>("Simulated", Boolean.class);
        Variable<Integer> runTime = new Variable<Integer>("RunTime", Integer.class);
        Variable<Float[]> fhr = new Variable<Float[]>("FHR", Float[].class);
        Variable<Integer[]> fetalHeight = new Variable<Integer[]>("fetalHeight", Integer[].class);
        Variable<Integer[]> signalToNoise = new Variable<Integer[]>("signalToNoise", Integer[].class);
        Variable<Integer[]> qfhr = new Variable<Integer[]>("QFHR", Integer[].class);
        Variable<Float[]> mhr = new Variable<Float[]>("MHR", Float[].class);
        Variable<Float[]> toco = new Variable<Float[]>("TOCO", Float[].class);
        Variable<String[]> signal = new Variable<String[]>("Signal", String[].class);
        Variable<String> startTime = new Variable<String>("StartTime", String.class);
        Variable<String> endTime = new Variable<String>("EndTime", String.class);
        Variable<Float> voltageStart = new Variable<Float>("VoltageStart", Float.class);
        Variable<Float> voltageEnd = new Variable<Float>("VoltageEnd", Float.class);
        OutputSkema outputSkema = new OutputSkema();
        outputSkema.addVariable(deviceId);
        outputSkema.addVariable(simulated);
        outputSkema.addVariable(runTime);
        outputSkema.addVariable(fhr);
        outputSkema.addVariable(fetalHeight);
        outputSkema.addVariable(signalToNoise);
        outputSkema.addVariable(qfhr);
        outputSkema.addVariable(mhr);
        outputSkema.addVariable(toco);
        outputSkema.addVariable(signal);
        outputSkema.addVariable(startTime);
        outputSkema.addVariable(endTime);
        outputSkema.addVariable(voltageStart);
        outputSkema.addVariable(voltageEnd);

        Skema skema = new Skema();

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        questionnaire.setOutputSkema(outputSkema);

        EndNode endNode = new EndNode(questionnaire, "endNode");

        // //////////////////////////////////////////////////////////////////////////////////////////

        SaveFileNode saveFileNode = new SaveFileNode(questionnaire, "saveFile");
        saveFileNode.setNext(endNode.getNodeName());

        // //////////////////////////////////////////////////////////////////////////////////////////

        MonicaDeviceNode monicaNode = new MonicaDeviceNode(questionnaire, "monicaNode");

        monicaNode.setNextFail(endNode.getNodeName());
        monicaNode.setNext(saveFileNode.getNodeName());

        monicaNode.setDeviceId(deviceId);
        monicaNode.setRunAsSimulator(simulated);
        monicaNode.setMeasuringTime(runTime);
        monicaNode.setFhr(fhr);
        monicaNode.setFetalHeight(fetalHeight);
        monicaNode.setSignalToNoise(signalToNoise);
        monicaNode.setQfhr(qfhr);
        monicaNode.setMhr(mhr);
        monicaNode.setToco(toco);
        monicaNode.setSignal(signal);
        monicaNode.setStartTime(startTime);
        monicaNode.setEndTime(endTime);
        monicaNode.setVoltageStart(voltageStart);
        monicaNode.setVoltageEnd(voltageEnd);

        // //////////////////////////////////////////////////////////////////////////////////////////

        IONode askSimulate = new IONode(questionnaire, "askSimulate");

        askSimulate.addElement(new TextViewElement(askSimulate, Util.getString(R.string.monica_ask_simulate, questionnaire)));

        {
            RadioButtonElement<Boolean> yesNo = new RadioButtonElement<Boolean>(askSimulate);
            @SuppressWarnings("unchecked")
            ValueChoice<Boolean>[] yesNoChoice = new ValueChoice[2];
            yesNoChoice[0] = new ValueChoice<Boolean>(true, Util.getString(R.string.monica_accept_simulate, questionnaire));
            yesNoChoice[1] = new ValueChoice<Boolean>(false, Util.getString(R.string.monica_decline_simulate, questionnaire));
            yesNo.setChoices(yesNoChoice);
            yesNo.setOutputVariable(simulated);
            askSimulate.addElement(yesNo);
        }

        {
            askSimulate.addElement(new TextViewElement(askSimulate, Util.getString(R.string.monica_enter_duration, questionnaire)));

            EditTextElement runTimeElm = new EditTextElement(askSimulate);
            runTimeElm.setOutputVariable(runTime);
            askSimulate.addElement(runTimeElm);
        }

        {
            ButtonElement button = new ButtonElement(askSimulate, Util.getString(R.string.default_proceed, questionnaire));
            button.setNext(monicaNode.getNodeName());
            button.setSkipValidation(true);
            askSimulate.addElement(button);
        }

        // //////////////////////////////////////////////////////////////////////////////////////////

        skema.addNode(endNode);
        skema.addNode(monicaNode);
        skema.addNode(askSimulate);
        skema.addNode(saveFileNode);

        skema.setStartNode(askSimulate.getNodeName());
        skema.setEndNode(endNode.getNodeName());

        skema.link();

        return skema;
    }
}
