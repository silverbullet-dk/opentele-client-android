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
import dk.silverbullet.telemed.questionnaire.expression.MultiplyExpression;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.AssignmentNode;
import dk.silverbullet.telemed.questionnaire.node.DecisionNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.MonicaDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Util;

public class TestJordemoder implements TestSkema {
    public static final String BUTTON_OK = "OK";

    public IONode getIONodeText(Questionnaire questionnaire, String name, String text, String button, String next) {
        IONode ioNode = new IONode(questionnaire, name);

        TextViewElement tve2 = new TextViewElement(ioNode);
        tve2.setText(text);
        ioNode.addElement(tve2);

        ButtonElement be2 = new ButtonElement(ioNode);
        be2.setText(button);
        be2.setGravity(ButtonElement.GRAVITY_RIGHT);
        be2.setNext(next);
        ioNode.addElement(be2);

        return ioNode;
    }

    public IONode getIONodeYesNo(Questionnaire questionnaire, String name, String text, String noNext, String yesNext) {

        return getIONodeYesNo(questionnaire, name, text, noNext, "Nej", yesNext, "Ja");
    }

    public IONode getIONodeYesNo(Questionnaire questionnaire, String name, String text, String leftNext,
            String leftText, String rightNext, String rightText) {
        IONode ioNode = new IONode(questionnaire, name);

        TextViewElement tve2 = new TextViewElement(ioNode);
        tve2.setText(text);
        ioNode.addElement(tve2);

        TwoButtonElement tbe = new TwoButtonElement(ioNode);
        tbe.setLeftNext(leftNext);
        tbe.setLeftText(leftText);
        tbe.setRightNext(rightNext);
        tbe.setRightText(rightText);
        ioNode.addElement(tbe);

        return ioNode;
    }

    public AssignmentNode<Boolean> getAssignmentNode(Questionnaire questionnaire, String name,
            Variable<Boolean> variable, boolean val, Node node) {

        AssignmentNode<Boolean> assignmentNode = new AssignmentNode<Boolean>(questionnaire, name, variable,
                new Constant<Boolean>(val));

        assignmentNode.setNext(node.getNodeName());

        return assignmentNode;
    }

    public Skema getSkema(Questionnaire questionnaire) throws UnknownNodeException {

        // ////////////////////////////////////////////////////////////////////////////////
        //
        // ////////////////////////////////////////////////////////////////////////////////
        // Variable
        OutputSkema outputSkema = new OutputSkema();
        Variable<Boolean> variableBevaegerBarnetsSig = new Variable<Boolean>("variableBevaegerBarnetsSig",
                Boolean.class);
        Variable<Boolean> variableErDuUtilpas = new Variable<Boolean>("variableErDuUtilpas", Boolean.class);
        Variable<Boolean> variableHarDuAandenoed = new Variable<Boolean>("variableHarDuAandenoed", Boolean.class);
        Variable<Boolean> variableOndtIMaven = new Variable<Boolean>("variableOndtIMaven", Boolean.class);
        Variable<Boolean> variableVaerreOejnene = new Variable<Boolean>("variableVaerreOejnene", Boolean.class);
        Variable<Boolean> variableFlimrenForOejnene = new Variable<Boolean>("variableFlimrenForOejnene", Boolean.class);
        Variable<Boolean> variableVaerreHovedpine = new Variable<Boolean>("variableVaerreHovedpine", Boolean.class);
        Variable<Boolean> variableHovedpine = new Variable<Boolean>("variableHovedpine", Boolean.class);
        Variable<Float> variableUrintest = new Variable<Float>("variableUrintest", Float.class);
        Variable<Boolean> variableKontakt = new Variable<Boolean>("variableKontakt", Boolean.class);
        Variable<Integer> variableDia = new Variable<Integer>("variableDia", Integer.class);
        Variable<Integer> variableSys = new Variable<Integer>("variableSys", Integer.class);
        Variable<Float> variableVaegt = new Variable<Float>("variableVaegt", Float.class);

        Variable<Float[]> variableMhr = new Variable<Float[]>("variableMhr", Float[].class);
        Variable<Float[]> variableFhr = new Variable<Float[]>("variableFhr", Float[].class);
        Variable<Integer[]> variableFmp = new Variable<Integer[]>("variableFmp", Integer[].class);
        Variable<Float[]> variableToco = new Variable<Float[]>("variableToco", Float[].class);
        Variable<Integer[]> variableQfhr = new Variable<Integer[]>("variableQfhr", Integer[].class);
        Variable<String[]> variableSignal = new Variable<String[]>("variableSignal", String[].class);
        Variable<String> variableStartTime = new Variable<String>("variableStartTime", String.class);
        Variable<String> variableEndTime = new Variable<String>("variableEndTime", String.class);
        Variable<Float> variableStartVoltage = new Variable<Float>("variableStartVoltage", Float.class);
        Variable<Float> variableEndVoltage = new Variable<Float>("variableEndVoltage", Float.class);

        outputSkema.addVariable(variableBevaegerBarnetsSig);
        outputSkema.addVariable(variableErDuUtilpas);
        outputSkema.addVariable(variableHarDuAandenoed);
        outputSkema.addVariable(variableOndtIMaven);
        outputSkema.addVariable(variableVaerreOejnene);
        outputSkema.addVariable(variableFlimrenForOejnene);
        outputSkema.addVariable(variableVaerreHovedpine);
        outputSkema.addVariable(variableHovedpine);
        outputSkema.addVariable(variableUrintest);
        outputSkema.addVariable(variableKontakt);
        outputSkema.addVariable(variableDia);
        outputSkema.addVariable(variableSys);
        outputSkema.addVariable(variableVaegt);

        outputSkema.addVariable(variableMhr);
        outputSkema.addVariable(variableFhr);
        outputSkema.addVariable(variableFmp);
        outputSkema.addVariable(variableToco);
        outputSkema.addVariable(variableQfhr);
        outputSkema.addVariable(variableSignal);
        outputSkema.addVariable(variableStartTime);
        outputSkema.addVariable(variableEndTime);
        outputSkema.addVariable(variableStartVoltage);
        outputSkema.addVariable(variableEndVoltage);

        // ////////////////////////////////////////////////////////////////////////////////
        EndNode end = new EndNode(questionnaire, "slut");

        // ////////////////////////////////////////////////////////////////////////////////
        IONode svarSendes = getIONodeText(questionnaire, "svarSendes", "Dine svar sendes nu til hospitalet", BUTTON_OK,
                end.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode kontaktJordemoder = getIONodeText(questionnaire, "kontaktJordemoder",
                "Kontakt venligst din jordemoder.", BUTTON_OK, svarSendes.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        // IONode paasaetMonica = getIONodeText(questionnaire, "paasaetMonica", "Påsæt Monica", BUTTON_OK,
        // svarSendes.getNodeName());
        MonicaDeviceNode paasaetMonica = new MonicaDeviceNode(questionnaire, "paasaetMonica");

        paasaetMonica.setFhr(variableFhr);
        paasaetMonica.setQfhr(variableQfhr);
        paasaetMonica.setMhr(variableMhr);
        paasaetMonica.setToco(variableToco);
        paasaetMonica.setSignal(variableSignal);
        paasaetMonica.setStartTime(variableStartTime);
        paasaetMonica.setEndTime(variableEndTime);
        paasaetMonica.setVoltageStart(variableStartVoltage);
        paasaetMonica.setVoltageEnd(variableEndVoltage);

        paasaetMonica.setNext(svarSendes.getNodeName());
        paasaetMonica.setNextFail(svarSendes.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        DecisionNode kontakt = new DecisionNode(questionnaire, "kontakt", variableKontakt);
        kontakt.setNext(kontaktJordemoder.getNodeName());
        kontakt.setNextFalse(paasaetMonica.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter1 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter1",
                variableKontakt, true, kontakt);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentBevaegerBarnetsSig = new AssignmentNode<Boolean>(questionnaire,
                "assignmentBevaegerBarnetsSig", variableBevaegerBarnetsSig, new Constant<Boolean>(false));

        assignmentBevaegerBarnetsSig.setNext(kontaktJordemoderEfter1.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode bevaegerBarnetsSig = getIONodeYesNo(questionnaire, "bevaegerBarnetsSig",
                "Bevæger barnets sig som det plejer?", assignmentBevaegerBarnetsSig.getNodeName(),
                kontakt.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter2 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter2",
                variableKontakt, true, bevaegerBarnetsSig);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentErDuUtilpas = new AssignmentNode<Boolean>(questionnaire,
                "assignmentErDuUtilpas", variableErDuUtilpas, new Constant<Boolean>(true));

        assignmentErDuUtilpas.setNext(kontaktJordemoderEfter2.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode erDuUtilpas = getIONodeYesNo(questionnaire, "erDuUtilpas", "Er du alment utilpas?",
                bevaegerBarnetsSig.getNodeName(), assignmentErDuUtilpas.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter3 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter3",
                variableKontakt, true, erDuUtilpas);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentHarDuAandenoed = new AssignmentNode<Boolean>(questionnaire,
                "assignmentHarDuAandenoed", variableHarDuAandenoed, new Constant<Boolean>(true));

        assignmentHarDuAandenoed.setNext(kontaktJordemoderEfter3.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode harDuAandenoed = getIONodeYesNo(questionnaire, "harDuAandenoed", "Har du åndenød?",
                erDuUtilpas.getNodeName(), assignmentHarDuAandenoed.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter4 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter4",
                variableKontakt, true, harDuAandenoed);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentOndtIMaven = new AssignmentNode<Boolean>(questionnaire,
                "assignmentOndtIMaven", variableOndtIMaven, new Constant<Boolean>(true));

        assignmentOndtIMaven.setNext(kontaktJordemoderEfter4.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode ondtIMaven = getIONodeYesNo(questionnaire, "ondtIMaven", "Har du ondt i øverste højre del af maven?",
                harDuAandenoed.getNodeName(), assignmentOndtIMaven.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter5 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter5",
                variableKontakt, true, ondtIMaven);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentVaerreOejnene = new AssignmentNode<Boolean>(questionnaire,
                "assignmentVaerreOejnene", variableVaerreOejnene, new Constant<Boolean>(true));

        assignmentVaerreOejnene.setNext(kontaktJordemoderEfter5.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode vaerreOejnene = getIONodeYesNo(questionnaire, "vaerreOejnene",
                "Er din flimren for øjnene værre end i går?", ondtIMaven.getNodeName(),
                assignmentVaerreOejnene.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentFlimrenForOejnene = new AssignmentNode<Boolean>(questionnaire,
                "assignmentFlimrenForOejnene", variableFlimrenForOejnene, new Constant<Boolean>(true));

        assignmentFlimrenForOejnene.setNext(vaerreOejnene.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode flimrenForOejnene = getIONodeYesNo(questionnaire, "flimrenForOejnene", "Har du flimren for øjnene?",
                ondtIMaven.getNodeName(), assignmentFlimrenForOejnene.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter6 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter6",
                variableKontakt, true, flimrenForOejnene);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentVaerreHovedpine = new AssignmentNode<Boolean>(questionnaire,
                "assignmentVaerreHovedpine", variableVaerreHovedpine, new Constant<Boolean>(true));

        assignmentVaerreHovedpine.setNext(kontaktJordemoderEfter6.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode vaerreHovedpine = getIONodeYesNo(questionnaire, "vaerreHovedpine", "Er din hovedpine værre end i går?",
                flimrenForOejnene.getNodeName(), assignmentVaerreHovedpine.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> assignmentHovedpine = new AssignmentNode<Boolean>(questionnaire, "assignmentHovedpine",
                variableHovedpine, new Constant<Boolean>(true));

        assignmentHovedpine.setNext(vaerreHovedpine.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode hovedpine = getIONodeYesNo(questionnaire, "hovedpine", "Har du hovedpine?",
                flimrenForOejnene.getNodeName(), assignmentHovedpine.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        // IONode vaegt = getIONodeText(questionnaire, "vaegt", "Vægt", BUTTON_OK, hovedpine.getNodeName());
        IONode vaegt = new IONode(questionnaire, "vaegt");
        TextViewElement tve = new TextViewElement(vaegt);
        tve.setText("Hvad er din vægt?");
        vaegt.addElement(tve);
        TextViewElement tve3 = new TextViewElement(vaegt);
        tve3.setText("Indtast her:");
        vaegt.addElement(tve3);
        EditTextElement ete2 = new EditTextElement(vaegt);
        ete2.setOutputVariable(variableVaegt);
        vaegt.addElement(ete2);
        ButtonElement be2 = new ButtonElement(vaegt);
        be2.setGravity(ButtonElement.GRAVITY_RIGHT);
        be2.setNext(hovedpine.getNodeName());
        be2.setText(BUTTON_OK);
        vaegt.addElement(be2);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter7 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter7",
                variableKontakt, true, vaegt);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter8 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter8",
                variableKontakt, true, vaegt);

        // ////////////////////////////////////////////////////////////////////////////////
        IONode proteinHoejere = getIONodeYesNo(questionnaire, "proteinHoejere", "Er proteintallet højere end vanligt?",
                vaegt.getNodeName(), kontaktJordemoderEfter8.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        Expression<Boolean> e1 = new Constant<Boolean>(true);
        DecisionNode decision2Protein = new DecisionNode(questionnaire, "decision2Protein", e1);
        decision2Protein.setNext(kontaktJordemoderEfter7.getNodeName());
        decision2Protein.setNextFalse(proteinHoejere.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        Expression<Boolean> e2 = new Constant<Boolean>(true);
        DecisionNode decisionSpor1Protein = new DecisionNode(questionnaire, "decisionSpor1Protein", e2);
        decisionSpor1Protein.setNext(vaegt.getNodeName());
        decisionSpor1Protein.setNextFalse(decision2Protein.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode urintest = new IONode(questionnaire, "urintest");
        TextViewElement tve2 = new TextViewElement(urintest);
        tve2.setText("Indtast svaret på din urintest");
        urintest.addElement(tve2);

        EditTextElement ete = new EditTextElement(urintest);
        ete.setOutputVariable(variableUrintest);
        urintest.addElement(ete);

        ButtonElement be = new ButtonElement(urintest);
        be.setGravity(ButtonElement.GRAVITY_RIGHT);
        be.setNext(decisionSpor1Protein.getNodeName());
        be.setText(BUTTON_OK);
        urintest.addElement(be);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> kontaktJordemoderEfter9 = getAssignmentNode(questionnaire, "kontaktJordemoderEfter9",
                variableKontakt, true, urintest);

        // ////////////////////////////////////////////////////////////////////////////////
        IONode btHoejere = getIONodeYesNo(questionnaire, "btHoejere", "Er dit blodtryk højere end vanligt?",
                urintest.getNodeName(), kontaktJordemoderEfter9.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        Expression<Integer> left = new AddExpression<Integer>(new MultiplyExpression<Integer>(variableDia,
                new Constant<Integer>(2)), variableSys);
        Expression<Integer> right2 = new AddExpression<Integer>(new Constant<Integer>(150),
                new MultiplyExpression<Integer>(new Constant<Integer>(100), new Constant<Integer>(2)));

        DecisionNode decisionBT140to150 = new DecisionNode(questionnaire, "decisionBT140to150", new LessThan<Integer>(
                left, right2));
        decisionBT140to150.setNext(btHoejere.getNodeName());
        decisionBT140to150.setNextFalse(kontaktJordemoderEfter9.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        Expression<Integer> right = new AddExpression<Integer>(new Constant<Integer>(140),
                new MultiplyExpression<Integer>(new Constant<Integer>(90), new Constant<Integer>(2)));

        DecisionNode decisionBTlt140 = new DecisionNode(questionnaire, "decisionBTlt140", new LessThan<Integer>(left,
                right));
        decisionBTlt140.setNext(urintest.getNodeName());
        decisionBTlt140.setNextFalse(decisionBT140to150.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        IONode blodtryk = new IONode(questionnaire, "blodtryk");
        TextViewElement tve6 = new TextViewElement(blodtryk);
        tve6.setText("Indtast værdier for dit blodtryk:");
        blodtryk.addElement(tve6);
        TextViewElement tve4 = new TextViewElement(blodtryk);
        tve4.setText("Systolisk:");
        blodtryk.addElement(tve4);
        EditTextElement ete4 = new EditTextElement(blodtryk);
        ete4.setOutputVariable(variableSys);
        blodtryk.addElement(ete4);
        TextViewElement tve5 = new TextViewElement(blodtryk);
        tve5.setText("Diatolisk:");
        blodtryk.addElement(tve5);
        EditTextElement ete3 = new EditTextElement(blodtryk);
        ete3.setOutputVariable(variableDia);
        blodtryk.addElement(ete3);
        ButtonElement be3 = new ButtonElement(blodtryk);
        be3.setGravity(ButtonElement.GRAVITY_RIGHT);
        be3.setNext(decisionBTlt140.getNodeName());
        be3.setText(BUTTON_OK);
        blodtryk.addElement(be3);

        // ////////////////////////////////////////////////////////////////////////////////
        AssignmentNode<Boolean> init = new AssignmentNode<Boolean>(questionnaire, "init", variableKontakt,
                new Constant<Boolean>(false));
        init.setNext(blodtryk.getNodeName());

        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////
        Skema skema = new Skema();
        skema.setStartNode(init.getNodeName());
        skema.setEndNode(end.getNodeName());
        skema.setCron("cron");
        skema.setName("skema-navn");
        skema.setVersion("1.0");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(svarSendes);
        skema.addNode(kontaktJordemoder);
        skema.addNode(paasaetMonica);
        skema.addNode(kontakt);
        skema.addNode(kontaktJordemoderEfter1);
        skema.addNode(assignmentBevaegerBarnetsSig);
        skema.addNode(bevaegerBarnetsSig);
        skema.addNode(kontaktJordemoderEfter2);
        skema.addNode(assignmentErDuUtilpas);
        skema.addNode(erDuUtilpas);
        skema.addNode(kontaktJordemoderEfter3);
        skema.addNode(assignmentHarDuAandenoed);
        skema.addNode(harDuAandenoed);
        skema.addNode(kontaktJordemoderEfter4);
        skema.addNode(assignmentOndtIMaven);
        skema.addNode(ondtIMaven);
        skema.addNode(kontaktJordemoderEfter5);
        skema.addNode(assignmentVaerreOejnene);
        skema.addNode(vaerreOejnene);
        skema.addNode(assignmentFlimrenForOejnene);
        skema.addNode(flimrenForOejnene);
        skema.addNode(kontaktJordemoderEfter6);
        skema.addNode(assignmentVaerreHovedpine);
        skema.addNode(vaerreHovedpine);
        skema.addNode(assignmentHovedpine);
        skema.addNode(hovedpine);
        skema.addNode(vaegt);
        skema.addNode(kontaktJordemoderEfter7);
        skema.addNode(kontaktJordemoderEfter8);
        skema.addNode(proteinHoejere);
        skema.addNode(decision2Protein);
        skema.addNode(decisionSpor1Protein);
        skema.addNode(urintest);
        skema.addNode(kontaktJordemoderEfter9);
        skema.addNode(btHoejere);
        skema.addNode(decisionBT140to150);
        skema.addNode(decisionBTlt140);
        skema.addNode(blodtryk);
        skema.addNode(init);

        skema.link();

        // ////////////////////////////////////////////////////////////////////////////////
        // end..
        // ////////////////////////////////////////////////////////////////////////////////

        return skema;
    }

    @Override
    public Skema getSkema() throws UnknownNodeException {
        Skema result = null;
        Questionnaire q = new Questionnaire(new QuestionnaireFragment());
        String json = Util.getGson().toJson(getSkema(q));
        result = Util.getGson().fromJson(json, Skema.class);
        return result;
    }
}
