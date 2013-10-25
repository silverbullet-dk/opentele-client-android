package dk.silverbullet.telemed.questionnaire.element;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;

public class TestElement extends Element {

    public TestElement(final IONode node) {
        super(node);
    }

    @Override
    public View getView() {
        Context ctx = getQuestionnaire().getActivity().getApplicationContext();
        Map<String, Variable<?>> vars = getQuestionnaire().getSkemaValuePool();
        StringBuffer sb2 = new StringBuffer();
        for (String name : vars.keySet()) {
            StringBuffer sb = new StringBuffer();
            Variable<?> obj = vars.get(name);
            sb.append(name).append(':').append(obj.getExpressionValue().getType());
            if (obj.getExpressionValue().getType().endsWith("[]")) {
                Object value = obj.getExpressionValue();
                sb.append('(').append(value.getClass().getName());
                if (value instanceof List) {
                    List<?> l = (List<?>) value;
                    sb.append('[').append(l.size()).append("])=");
                    addAll(sb, l);
                } else if (value instanceof Iterable<?>) {
                    sb.append('=');
                    addAll(sb, (Iterable<?>) value);
                }
            } else {
                sb.append('=').append(obj.getExpressionValue());
            }

            if (sb2.length() > 0) {
                sb2.append('\n');
            }
            sb2.append(sb);
        }
        TextView tv = new TextView(ctx);
        tv.setText(sb2);
        return tv;
    }

    private void addAll(StringBuffer sb, Iterable<?> i) {
        boolean first = true;
        for (Object object : i) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            String s = object.toString();
            if (sb.length() + s.length() > 150) {
                sb.append("...");
                break;
            }
            sb.append(s);
        }
    }

    @Override
    public void leave() {
        // Do nothing
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        // Do nothing
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws UnknownVariableException {
        // Done1
    }

    @Override
    public boolean validates() {
        return true;
    }
}
