package dk.silverbullet.telemed;

import static org.junit.Assert.fail;

import java.util.Date;

import dk.silverbullet.telemed.utils.Json;
import org.junit.Test;

import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;

public class TestOutputJson {

    @Test
    public void test() {
        OutputSkema out = new OutputSkema();
        out.setName("A name");
        out.setVersion("1.0");
        out.setDate(new Date());

        out.addVariable(new Variable<Integer>("aInteger", 100));
        out.addVariable(new Variable<Float>("aFloat", 100f));
        out.addVariable(new Variable<String>("aString", "string"));
        out.addVariable(new Variable<Double>("aDouble", 100.1d));
        out.addVariable(new Variable<Integer>("aInteger", 200));

        out.addVariable(new Variable<Integer[]>("aIntegerA", new Integer[] { 100, 100 }));
        out.addVariable(new Variable<Float[]>("aFloatA", new Float[] { 100f, 100f }));
        out.addVariable(new Variable<String[]>("aStringA", new String[] { "string", "string2" }));
        out.addVariable(new Variable<Double[]>("aDoubleA", new Double[] { 100.1d, 100.2d }));
        out.addVariable(new Variable<Integer[]>("aIntegerA", new Integer[] { 200, 201 }));

        String json = Json.print(out);
        System.out.println(json);

        try {
            OutputSkema newOut = Json.parse(json, OutputSkema.class);
            System.out.println(newOut);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
    }
}
