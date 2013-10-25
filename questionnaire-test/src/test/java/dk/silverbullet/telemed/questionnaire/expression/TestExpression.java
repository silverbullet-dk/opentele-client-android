package dk.silverbullet.telemed.questionnaire.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.node.AssignmentNode;
import dk.silverbullet.telemed.utils.Util;

public class TestExpression {

    private Gson gson;

    @Before
    public void before() {
        gson = Util.getGson();
    }

    @Test
    public void testValueSerialisation() {
        Constant<?> v;

        v = new Constant<Integer>(124);
        testReserialisation(Integer.class, v, v.getType(), 124);

        v = new Constant<Float>(124f);
        testReserialisation(Float.class, v, v.getType(), 124F);

        v = new Constant<Long>(123L);
        testReserialisation(Long.class, v, v.getType(), 123L);

        v = new Constant<Double>(123.456);
        testReserialisation(Double.class, v, v.getType(), 123.456);

        Object[] test = new Integer[] { 1, 2, 4 };
        v = new Constant<Integer[]>((Integer[]) test);
        testReserialisation(Integer[].class, v, v.getType(), (Integer[]) test);

        test = new String[] { "Her", "er", "en", "test" };
        v = new Constant<String[]>((String[]) test);
        testReserialisation(String[].class, v, v.getType(), (String[]) test);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddMulSubDiv() throws Exception {
        // Test evaluating this expression:
        // (4*a+24)/(b-12)
        // using integer and double values for variables a and b.

        Expression<Number> n4 = new Constant<Number>(4);
        Expression<Number> n24 = new Constant<Number>(24);
        Expression<Number> n12 = new Constant<Number>(12);
        Variable<Number> a = new Variable<Number>("A", 42);
        Variable<Number> b = new Variable<Number>("B", 67);

        Expression<Number> ex = new DivideExpression<Number>(new AddExpression<Number>(new MultiplyExpression<Number>(
                n4, a), n24), new SubtractExpression<Number>(b, n12));
        System.out.println(ex + "=" + ex.evaluate());

        String s = gson.toJson(ex, Expression.class);
        System.out.println("*********** JSON Expression for " + ex + ":");
        System.out.println(s);
        ex = (Expression<Number>) gson.fromJson(s, Expression.class);
        System.out.println("*********** JSON Expression parsed " + ex);

        Map<String, Variable<?>> varPool = new HashMap<String, Variable<?>>();
        varPool.put(a.getName(), a);
        varPool.put(b.getName(), b);
        ex.link(varPool);
        // Test expression using integers:
        for (int aValue = 5; aValue < 1000; aValue *= 2) {
            a.setValue(aValue);
            for (int bValue = 100; bValue > 21; bValue -= 5) {
                b.setValue(bValue);
                assertEquals((4 * aValue + 24) / (bValue - 12), ((Integer) ex.evaluate()).intValue());
            }
        }

        // Test same unmodified expression using double:
        for (double aValue = 5.355434345432; aValue < 100; aValue *= 12.34) {
            a.setValue(aValue);
            for (double bValue = 32.432; bValue > 21; bValue *= 0.8983) {
                b.setValue(bValue);
                assertEquals((4 * aValue + 24) / (bValue - 12), ((Double) ex.evaluate()).doubleValue(), 0); // DELTA? :)

            }
        }
    }

    @Test
    public void testVariableLink() throws VariableLinkFailedException {
        Variable<Integer> poolA = new Variable<Integer>("A", Integer.class);
        Variable<Integer> a1 = new Variable<Integer>("A", Integer.class);
        Variable<Integer> a2 = new Variable<Integer>("A", Integer.class);
        Map<String, Variable<?>> variablePool = new HashMap<String, Variable<?>>();
        variablePool.put(poolA.getName(), poolA);

        Expression<Integer> e = new AddExpression<Integer>(a1, a2);
        e.link(variablePool);
        poolA.setValue(new Constant<Integer>(42));
        assertEquals(84, e.evaluate().intValue());

    }

    @Data
    class Blob {
        @Expose
        Expression<?> e;
    }

    // @Ignore
    @SuppressWarnings("unchecked")
    @Test
    public void testGsonSerialise() {
        Expression<Integer> n24 = new Constant<Integer>(24);
        Constant<Integer> n12 = new Constant<Integer>(12);
        Variable<Integer> v1 = new Variable<Integer>("CoolFactor", Integer.class);
        Expression<Integer> a = new AddExpression<Integer>(n12, n24);
        Expression<Integer> s = new SubtractExpression<Integer>(a, v1);

        // v1.setValue(1000);
        System.out.println(v1);

        System.out.println("getValue: " + n12.getValue());
        String js = gson.toJson(s, Expression.class);
        System.out.println("JS= " + js);

        Expression<?> e = gson.fromJson(js, Expression.class);

        System.out.println(e);

        System.out.println();
        List<Integer> il = new LinkedList<Integer>();
        il.add(10);
        il.add(20);
        il.add(40);
        il.add(40);
        Integer[] ia = new Integer[] { 11, 22, 33 };
        String ias = gson.toJson(ia, Integer[].class);
        System.out.println("JSON array: " + ias);
        System.out.println("Int array type: " + ia.getClass().getName());
        System.out.println();
        Constant<Integer[]> v = new Constant<Integer[]>(ia);
        System.out.println("v=" + v.toString());
        System.out.println("v.type=" + v.getType());
        js = gson.toJson(v, Expression.class);
        System.out.println("int array: " + js);

        Constant<Integer[]> v0 = (Constant<Integer[]>) gson.fromJson(js, Expression.class);

        System.out.println(v0);
        Integer[] zz = (Integer[]) v0.getValue();
        for (Integer integer : zz) {
            System.out.println(integer);
        }
        System.out.println();

        System.out.println(gson.toJson(v1, Expression.class));
        System.out.println(gson.toJson(e, Expression.class));

        AssignmentNode<Integer> anode = new AssignmentNode<Integer>(null, null, v1, (Expression<Integer>) e);
        System.out.println("***********' ASSIGNMENT:");
        System.out.println(gson.toJson(anode, Object.class));

        Blob b = new Blob();
        b.e = e;
        System.out.println("***********' BLOB:");
        System.out.println(gson.toJson(b, Object.class));
        assertTrue(true);
    }

    @SuppressWarnings("unchecked")
    private <T> void testReserialisation(Class<T> expectedClass, Constant<?> v, String typeName, T expectedValue) {
        typeName = expectedClass.getName().replaceAll(".*\\.", "").replaceAll(";\\z", "");
        if (expectedClass.isArray())
            typeName += "[]";
        assertEquals("Type name check", typeName, v.getType());
        assertEquals("Value getValue type check", v.getValue().getClass(), expectedValue.getClass());
        if (expectedClass.isArray()) {
            assertTrue("Array compare", Arrays.equals((T[]) v.getValue(), (T[]) expectedValue));
        } else {
            assertEquals("Value getValue check", v.getValue(), expectedValue);
        }

        // Serialise
        String s = gson.toJson(v, Expression.class);
        System.out.println("*** " + s);
        // ..and de-serialise from the string
        Constant<?> v2 = (Constant<?>) gson.fromJson(s, Expression.class);

        // Re-check the de-serialised value:
        assertEquals("Type name check", typeName, v2.getType());
        assertEquals("Value getValue type check", v2.getValue().getClass(), expectedValue.getClass());
        if (expectedClass.isArray()) {
            assertTrue("Array compare", Arrays.equals((T[]) v2.getValue(), (T[]) expectedValue));
        } else {
            assertEquals("Value getValue check", v2.getValue(), expectedValue);
        }
    }
}