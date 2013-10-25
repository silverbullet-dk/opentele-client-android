package dk.silverbullet.telemed.questionnaire.skema;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.SetShowUploadDebugNode;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Util;

public class SetShowUploadDebugNodeSkema implements SkemaDef {

    @SuppressWarnings("unchecked")
    @Override
    public Skema getSkema(Questionnaire questionnaire) {

        Variable<?> showUploadDebugNode = questionnaire.getValuePool().get(Util.VARIABLE_SHOW_UPLOAD_DEBUG);

        if (null == showUploadDebugNode)
            showUploadDebugNode = new Variable<Boolean>(Util.VARIABLE_SHOW_UPLOAD_DEBUG, Boolean.class);

        OutputSkema outputSkema = new OutputSkema();
        outputSkema.addVariable(showUploadDebugNode);

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addVariable(output);
            // skema.addVariable(output);
        }

        // ////////////////////////////////////////////////////////////////////////////////

        EndNode end = new EndNode(questionnaire, "End");

        SetShowUploadDebugNode setShow = new SetShowUploadDebugNode(questionnaire, "setShow");
        setShow.setNextNode(end);
        setShow.setShowUploadDebugNode((Variable<Boolean>) showUploadDebugNode);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("SERVERIP");
        skema.setStartNode(setShow.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(setShow);

        return skema;
    }
}
