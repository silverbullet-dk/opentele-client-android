package dk.silverbullet.telemed.questionnaire.skema;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.DebugListPoolNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.IOSkemaMenuNode;
import dk.silverbullet.telemed.questionnaire.node.RunQuestionnaireNode;
import dk.silverbullet.telemed.questionnaire.node.UploadNode;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Util;

public class RunSkema implements SkemaDef {

    @Override
    public Skema getSkema(Questionnaire questionnaire) {
        // Variable
        OutputSkema outputSkema = new OutputSkema();

        Variable<String> skemaName = new Variable<String>("skema", String.class);

        outputSkema.addVariable(skemaName);

        // ////////////////////////////////////////////////////////////////////////////////
        EndNode end = new EndNode(questionnaire, "END");

        // ////////////////////////////////////////////////////////////////////////////////
        UploadNode uploadNode = new UploadNode(questionnaire, "UPLOAD");
        uploadNode.setNextNode(end);
        uploadNode.setHideTopPanel(true);

        // ////////////////////////////////////////////////////////////////////////////////
        DebugListPoolNode debugNode = new DebugListPoolNode(questionnaire, "MAIN");
        debugNode.setNextNode(uploadNode);

        // ////////////////////////////////////////////////////////////////////////////////
        IONode uploadValg = new IONode(questionnaire, "uploadValg");
        TextViewElement twe = new TextViewElement(uploadValg);
        twe.setText(Util.getString(R.string.run_skema_do_you_want_to_send_data, questionnaire));
        uploadValg.addElement(twe);

        TwoButtonElement tbe = new TwoButtonElement(uploadValg);
        tbe.setLeftText(Util.getString(R.string.default_no, questionnaire));
        tbe.setLeftNextNode(end);
        tbe.setRightText(Util.getString(R.string.default_yes, questionnaire));
        uploadValg.addElement(tbe);

        @SuppressWarnings("unchecked")
        Variable<Boolean> showUploadDebugNode = (Variable<Boolean>) questionnaire.getValuePool().get(
                Util.VARIABLE_SHOW_UPLOAD_DEBUG);

        if (showUploadDebugNode.getExpressionValue().getValue()) {
            tbe.setRightNextNode(debugNode);
        } else {
            tbe.setRightNextNode(uploadNode);
        }

        // ////////////////////////////////////////////////////////////////////////////////
        RunQuestionnaireNode run = new RunQuestionnaireNode(questionnaire, "RUN");
        run.setSkemaName(skemaName);
        run.setNextNode(uploadValg);

        // ////////////////////////////////////////////////////////////////////////////////
        IOSkemaMenuNode ioSkemaMenuNode = new IOSkemaMenuNode(questionnaire, "SKEMA-MENU");
        ioSkemaMenuNode.setNextNode(run);
        ioSkemaMenuNode.setSkemaName(skemaName);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("RUN-SKEMA");
        skema.setStartNode(ioSkemaMenuNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(uploadNode);
        skema.addNode(debugNode);
        skema.addNode(run);
        skema.addNode(ioSkemaMenuNode);

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addVariable(output);
            skema.addVariable(output);
        }

        return skema;
    }
}
