package dk.silverbullet.telemed.deleteme;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestHenrik implements TestSkema {

    private static String json = "{\n" + //
            "\n" + //
            "   \"name\":\"Over 18 test\",\n" + //
            "   \"version\":\"0.1\",\n" + //
            "   \"cron\":\"0 12 * * 2,5\",\n" + //
            "   \"startNode\":\"9\",\n" + //
            "   \"endNode\":\"10\",\n" + //
            "   \"nodes\":[\n" + //
            "      {\n" + //
            "\n" + //
            "         \"IONode\":{\n" + //
            "            \"title\":\"Til lykke, du er jo sund og rask!\",\n" + //
            "            \"nodeName\":\"15\",\n" + //
            "            \"elements\":[\n" + //
            "               {\n" + //
            "                  \"TextViewElement\":{\n" + //
            "                     \"text\":\"Til lykke, du er jo sund og rask!\"\n" + //
            "                  }\n" + //
            "               },\n" + //
            "               {\n" + //
            "                  \"ButtonElement\":{\n" + //
            "                     \"text\":\"Næste\",\n" + //
            "                     \"gravity\":\"center\",\n" + //
            "                     \"next\":\"10\"\n" + //
            "                  }\n" + //
            "               }\n" + //
            "            ]\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "\n" + //
            "         \"IONode\":{\n" + //
            "            \"title\":\"Du skulle nnu spise nogle piratos!\",\n" + //
            "            \"nodeName\":\"16\",\n" + //
            "\n" + //
            "            \"elements\":[\n" + //
            "               {\n" + //
            "                  \"TextViewElement\":{\n" + //
            "                     \"text\":\"Du skulle nnu spise nogle piratos!\"\n" + //
            "                  }\n" + //
            "               },\n" + //
            "               {\n" + //
            "                  \"ButtonElement\":{\n" + //
            "                     \"text\":\"Næste\",\n" + //
            "                     \"gravity\":\"center\",\n" + //
            "                     \"next\":\"10\"\n" + //
            "                  }\n" + //
            "               }\n" + //
            "            ]\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "\n" + //
            "         \"title\":\"Indtast dit blodtryk\",\n" + //
            "         \"nodeName\":\"13\",\n" + //
            "         \"next\":\"11\",\n" + //
            "         \"elements\":[\n" + //
            "            {\n" + //
            "               \"TextViewElement\":{\n" + //
            "\n" + //
            "                  \"text\":\"Indtast dit blodtryk\"\n" + //
            "               }\n" + //
            "            },\n" + //
            "            {\n" + //
            "               \"EditTextElement\":{\n" + //
            "                  \"defaultText\":\"\",\n" + //
            "                  \"outputName\":\"13#FIELD\"\n" + //
            "\n" + //
            "               }\n" + //
            "            },\n" + //
            "            {\n" + //
            "               \"ButtonElement\":{\n" + //
            "                  \"text\":\"Næste\",\n" + //
            "                  \"gravity\":\"center\",\n" + //
            "                  \"next\":\"11\"\n" + //
            "               }\n" + //
            "            }\n" + //
            "         ]\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"IONode\":{\n" + //
            "            \"nodeName\":\"9\",\n" + //
            "            \"elements\":[\n" + //
            "               {\n" + //
            "                  \"TextViewElement\":{\n" + //
            "                     \"text\":\"Er du over 18 år gammel?\"\n" + //
            "                  }\n" + //
            "               },\n" + //
            "               {\n" + //
            "                  \"TwoButtonElement\":{\n" + //
            "                     \"leftText\":\"Ja\",\n" + //
            "                     \"leftNext\":\"AN12\",\n" + //
            "                     \"rightText\":\"Nej\",\n" + //
            "                     \"rightNext\":\"AN14\"\n" + //
            "                  }\n" + //
            "               }\n" + //
            "            ]\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"AssignmentNode\":{\n" + //
            "            \"nodeName\":\"AN12\",\n" + //
            "            \"next\":12,\n" + //
            "            \"Variable\":{\n" + //
            "               \"name\":\"9#FIELD\",\n" + //
            "               \"value\":\"true\"\n" + //
            "            }\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"AssignmentNode\":{\n" + //
            "            \"nodeName\":\"AN14\",\n" + //
            "            \"next\":14,\n" + //
            "            \"Variable\":{\n" + //
            "               \"name\":\"9#FIELD\",\n" + //
            "               \"value\":\"false\"\n" + //
            "            }\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"EndNode\":{\n" + //
            "            \"nodeName\":\"10\"\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"DecisionNode\":{\n" + //
            "            \"nodeName\":\"11\",\n" + //
            "            \"next\":\"15\",\n" + //
            "            \"nextFalse\":\"16\",\n" + //
            "\n" + //
            "            \"expression\":{\n" + //
            "               \"gt\":{\n" + //
            "                  \"left\":{\n" + //
            "                     \"type\":\"Integer\",\n" + //
            "                     \"value\":130\n" + //
            "                  },\n" + //
            "                  \"right\":{\n" + //
            "                     \"type\":\"name\",\n" + //
            "                     \"value\":\"13#FIELD\"\n" + //
            "\n" + //
            "                  }\n" + //
            "               }\n" + //
            "            }\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"IONode\":{\n" + //
            "            \"title\":\"Ældre end 18\",\n" + //
            "            \"nodeName\":\"12\",\n" + //
            "\n" + //
            "            \"elements\":[\n" + //
            "               {\n" + //
            "                  \"TextViewElement\":{\n" + //
            "                     \"text\":\"Ældre end 18\"\n" + //
            "                  }\n" + //
            "               },\n" + //
            "               {\n" + //
            "                  \"ButtonElement\":{\n" + //
            "                     \"text\":\"Næste\",\n" + //
            "                     \"gravity\":\"center\",\n" + //
            "                     \"next\":\"13\"\n" + //
            "                  }\n" + //
            "               }\n" + //
            "            ]\n" + //
            "\n" + //
            "         }\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"IONode\":{\n" + //
            "            \"title\":\"Ikke for børn. Kom tilbage når du er blevet voksen!\",\n" + //
            "            \"nodeName\":\"14\",\n" + //
            "            \"elements\":[\n" + //
            "               {\n" + //
            "                  \"TextViewElement\":{\n" + //
            "\n" + //
            "                     \"text\":\"Ikke for børn. Kom tilbage når du er blevet voksen!\"\n" + //
            "                  }\n" + //
            "               },\n" + //
            "               {\n" + //
            "                  \"ButtonElement\":{\n" + //
            "                     \"text\":\"Næste\",\n" + //
            "                     \"gravity\":\"center\",\n" + //
            "                     \"next\":\"10\"\n" + //
            "                  }\n" + //
            "               }\n" + //
            "            ]\n" + //
            "         }\n" + //
            "      }\n" + //
            "   ],\n" + //
            "   \"output\":[\n" + //
            "      {\n" + //
            "         \"name\":\"13#FIELD\",\n" + //
            "         \"type\":\"Integer\"\n" + //
            "      },\n" + //
            "      {\n" + //
            "         \"name\":\"9#FIELD\",\n" + //
            "         \"type\":\"Boolean\"\n" + //
            "      }\n" + //
            "   ]\n" + //
            "}";

    public Skema getSkema() {
        return Json.parse(json, Skema.class);
    }

    public Skema getSkema(Questionnaire questionnaire) throws UnknownNodeException {
        Skema skema = Json.parse(json, Skema.class);
        skema.link();
        skema.setQuestionnaire(questionnaire);
        for (Variable<?> output : skema.getOutput())
            questionnaire.addSkemaVariable(output);

        return skema;
    }
}
