package dk.silverbullet.telemed.deleteme;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.AddExpression;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Expression;
import dk.silverbullet.telemed.questionnaire.expression.LessThan;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.AssignmentNode;
import dk.silverbullet.telemed.questionnaire.node.DecisionNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestPiratos implements TestSkema {

    public static final String TAG = Util.getTag(TestPiratos.class);

    public Skema getSkemaFromJSON(Questionnaire questionnaire) throws UnknownNodeException, VariableLinkFailedException {
        Skema skema = getSkema();

        skema.link();
        skema.setQuestionnaire(questionnaire);
        for (Variable<?> output : skema.getOutput())
            questionnaire.addSkemaVariable(output);

        for (Node node : skema.getNodes()) {
            node.linkVariables(questionnaire.getSkemaValuePool());
        }
        questionnaire.setStartNode(skema.getStartNodeNode());
        return skema;
    }

    public Skema getSkema(Questionnaire questionnaire) throws UnknownNodeException {

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////
        // Variable
        OutputSkema outputSkema = new OutputSkema();
        Variable<Integer> outputAlder = new Variable<Integer>("alder", Integer.class);
        Variable<Integer> outputBlodtryk = new Variable<Integer>("blodtryk", Integer.class);

        outputSkema.addVariable(outputAlder);
        outputSkema.addVariable(outputBlodtryk);

        Variable<Integer> alder = new Variable<Integer>("alder", 18);
        Variable<Integer> blodtryk = new Variable<Integer>("blodtryk", 17);

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////
        EndNode end = new EndNode(questionnaire, "slut");

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////
        IONode ikkeForBoern = new IONode(questionnaire, "IkkeForBørn");

        TextViewElement tve2 = new TextViewElement(ikkeForBoern);
        tve2.setText("Ikke for børn. Kom tilbage når du er blevet voksen!");
        ikkeForBoern.addElement(tve2);

        ButtonElement be2 = new ButtonElement(ikkeForBoern);
        be2.setText("Farvel");
        be2.setGravity(ButtonElement.GRAVITY_CENTER);
        be2.setNext(end.getNodeName());
        ikkeForBoern.addElement(be2);

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////
        IONode sundOgRask = new IONode(questionnaire, "sundOgRask");

        TextViewElement tve3 = new TextViewElement(sundOgRask);
        tve3.setText("Til lykke, du er jo sund og rask!");
        sundOgRask.addElement(tve3);

        ButtonElement be3 = new ButtonElement(sundOgRask);
        be3.setText("Farvel");
        be3.setGravity(ButtonElement.GRAVITY_CENTER);
        be3.setNext(end.getNodeName());
        sundOgRask.addElement(be3);

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////
        IONode spisPiratos = new IONode(questionnaire, "spisPiratos");

        TextViewElement tve4 = new TextViewElement(spisPiratos);
        tve4.setText("Du skulle nnu spise nogle piratos!");
        tve4.setText("Du skulle nok spise et par poser piratos!");
        spisPiratos.addElement(tve4);

        ButtonElement be4 = new ButtonElement(spisPiratos);
        be4.setText("Farvel");
        be4.setGravity(ButtonElement.GRAVITY_CENTER);
        be4.setNext(end.getNodeName());
        spisPiratos.addElement(be4);

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////

        Expression<Boolean> e1 = new LessThan<Integer>(blodtryk, new AddExpression<Integer>(alder,
                new Constant<Integer>(90)));

        DecisionNode blodtrykForLavt = new DecisionNode(questionnaire, "blodtrykForLavt", e1);

        blodtrykForLavt.setNext(spisPiratos.getNodeName());
        blodtrykForLavt.setNextFalse(sundOgRask.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////

        IONode indtastBlodtryk = new IONode(questionnaire, "indtastBlodtryk");

        TextViewElement tve5 = new TextViewElement(indtastBlodtryk);
        tve5.setText("Indtast dit blodtryk:");
        indtastBlodtryk.addElement(tve5);

        EditTextElement e2 = new EditTextElement(indtastBlodtryk);
        e2.setOutputVariable(outputBlodtryk);
        indtastBlodtryk.addElement(e2);

        ButtonElement be5 = new ButtonElement(indtastBlodtryk);
        be5.setText("Næste");
        be5.setGravity(ButtonElement.GRAVITY_CENTER);
        be5.setNext(blodtrykForLavt.getNodeName());
        indtastBlodtryk.addElement(be5);

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////

        AssignmentNode<Integer> a1 = new AssignmentNode<Integer>(questionnaire, "sætAlderTil18", alder,
                new Constant<Integer>(18));

        a1.setNext(indtastBlodtryk.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////

        IONode aeldreEnd18 = new IONode(questionnaire, "ældreEnd18");

        TextViewElement tv5 = new TextViewElement(aeldreEnd18);
        tv5.setText("Er du over 18 år gammel?");
        aeldreEnd18.addElement(tv5);

        TwoButtonElement tbe1 = new TwoButtonElement(aeldreEnd18);
        tbe1.setLeftText("Nej");
        tbe1.setLeftNext(ikkeForBoern.getNodeName());
        tbe1.setRightText("Ja");
        tbe1.setRightNext(a1.getNodeName());
        aeldreEnd18.addElement(tbe1);

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////
        Skema skema = new Skema();
        skema.setStartNode(aeldreEnd18.getNodeName());
        skema.setEndNode(end.getNodeName());
        skema.setName("skema-navn");
        skema.setVersion("1.0");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(ikkeForBoern);
        skema.addNode(aeldreEnd18);
        skema.addNode(a1);
        skema.addNode(indtastBlodtryk);
        skema.addNode(blodtrykForLavt);
        skema.addNode(spisPiratos);
        skema.addNode(sundOgRask);

        skema.link();

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////

        return skema;
    }

    @Override
    public Skema getSkema() {
        Questionnaire q = new Questionnaire(new QuestionnaireFragment());
        try {
            String json = Json.print(getSkema(q));
            return Json.parse(json, Skema.class);
        } catch (UnknownNodeException e) {
            e.printStackTrace();
        }
        return null;

    }
}