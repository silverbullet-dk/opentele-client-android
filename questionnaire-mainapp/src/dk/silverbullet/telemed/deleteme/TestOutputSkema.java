package dk.silverbullet.telemed.deleteme;

import java.util.Date;

import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestOutputSkema {
    public static String getOutputSkema() {
        OutputSkema out = new OutputSkema();
        out.setName("JSON KIM");
        out.setVersion("0.1");
        out.setPatientId("10");
        out.setQuestionnaireId(1L);
        out.setDate(new Date());

        // out.setDate(new SimpleDateFormat("yyyyMMddhhmm").format(new Date()));

        out.addVariable(new Variable<Integer>("aInteger", 100));
        out.addVariable(new Variable<Float>("aFloat", 100f));
        out.addVariable(new Variable<String>("aString", "string"));
        out.addVariable(new Variable<Double>("aDouble", 100.1d));
        out.addVariable(new Variable<Integer>("aInteger", 200));

        // out.addVariable(new Variable<Integer[]>("aIntegerA", new Integer[] { 100, 100 }));
        // out.addVariable(new Variable<Float[]>("aFloatA", new Float[] { 100f, 100f }));
        // out.addVariable(new Variable<String[]>("aStringA", new String[] { "string", "string2" }));
        // out.addVariable(new Variable<Double[]>("aDoubleA", new Double[] { 100.1d, 100.2d }));
        // out.addVariable(new Variable<Integer[]>("aIntegerA", new Integer[] { 200, 201 }));

        return Json.print(out);
    }
}
