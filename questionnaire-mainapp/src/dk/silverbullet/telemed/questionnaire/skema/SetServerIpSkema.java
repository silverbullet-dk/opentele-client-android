package dk.silverbullet.telemed.questionnaire.skema;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.SetServerIpNode;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Util;

public class SetServerIpSkema implements SkemaDef {

    @SuppressWarnings("unchecked")
    @Override
    public Skema getSkema(Questionnaire questionnaire) {
        // Variable

        Variable<?> serverIP = questionnaire.getValuePool().get(Util.VARIABLE_SERVER_IP);

        if (null == serverIP)
            serverIP = new Variable<String>(Util.VARIABLE_SERVER_IP, String.class);

        OutputSkema outputSkema = new OutputSkema();
        outputSkema.addVariable(serverIP);

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addVariable(output);
            // skema.addVariable(output);
        }

        // ////////////////////////////////////////////////////////////////////////////////

        EndNode end = new EndNode(questionnaire, "End");

        SetServerIpNode setServerIpNode = new SetServerIpNode(questionnaire, "setSErverIP");
        setServerIpNode.setNextNode(end);
        setServerIpNode.setServerIP((Variable<String>) serverIP);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("SERVERIP");
        skema.setStartNode(setServerIpNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(setServerIpNode);

        return skema;
    }
}
