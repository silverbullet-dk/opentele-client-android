package dk.silverbullet.telemed.deleteme;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.AssignmentNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.MonicaDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class MonicaSkemaUserLimited implements TestSkema {
    @Override
    public Skema getSkema() throws UnknownNodeException {
        Questionnaire q = new Questionnaire(new QuestionnaireFragment());
        String json = Json.print(getInternSkema(q));
        return Json.parse(json, Skema.class);
    }

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {

        Variable<String> deviceId = new Variable<String>("DeviceID", String.class);
        Variable<Boolean> simulated = new Variable<Boolean>("Simulated", Boolean.class);
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

        EndNode endNode = new EndNode(questionnaire, "endNode");

        // //////////////////////////////////////////////////////////////////////////////////////////

        MonicaDeviceNode monicaNode = new MonicaDeviceNode(questionnaire, "monicaNode");

        monicaNode.setNextFail(endNode.getNodeName());
        monicaNode.setNext(endNode.getNodeName());

        monicaNode.setDeviceId(deviceId);
        monicaNode.setRunAsSimulator(simulated);
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

        AssignmentNode<Boolean> setSimulateToTrue = new AssignmentNode<Boolean>(questionnaire, "setSimulateToTrue",
                simulated, new Constant<Boolean>(true));
        setSimulateToTrue.setNext(monicaNode.getNodeName());

        // //////////////////////////////////////////////////////////////////////////////////////////

        IONode askSimulate = new IONode(questionnaire, "askSimulate");
        TextViewElement simulateYesNo = new TextViewElement(askSimulate);
        simulateYesNo.setText(Util.getString(R.string.monica_ask_simulate, questionnaire));
        askSimulate.addElement(simulateYesNo);
        TwoButtonElement simulateYesNoButtons = new TwoButtonElement(askSimulate);
        simulateYesNoButtons.setLeftNext(monicaNode.getNodeName());
        simulateYesNoButtons.setLeftText(Util.getString(R.string.default_no, questionnaire));
        simulateYesNoButtons.setRightNext(setSimulateToTrue.getNodeName());
        simulateYesNoButtons.setRightText(Util.getString(R.string.default_yes, questionnaire));
        askSimulate.addElement(simulateYesNoButtons);

        // //////////////////////////////////////////////////////////////////////////////////////////

        AssignmentNode<Boolean> setSimulateToFalse = new AssignmentNode<Boolean>(questionnaire, "setSimulateToFalse",
                simulated, new Constant<Boolean>(false));
        setSimulateToFalse.setNext(askSimulate.getNodeName());

        // //////////////////////////////////////////////////////////////////////////////////////////

        skema.addNode(endNode);
        skema.addNode(monicaNode);
        skema.addNode(setSimulateToFalse);
        skema.addNode(setSimulateToTrue);
        skema.addNode(askSimulate);

        skema.setStartNode(setSimulateToFalse.getNodeName());
        skema.setEndNode(endNode.getNodeName());

        skema.link();

        return skema;
    }
}
