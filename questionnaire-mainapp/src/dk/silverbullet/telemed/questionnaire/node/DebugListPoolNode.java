package dk.silverbullet.telemed.questionnaire.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class DebugListPoolNode extends Node {

    private static final String TAG = Util.getTag(DebugListPoolNode.class);

    private Node nextNode;

    public DebugListPoolNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {

        inflateView(questionnaire.getActivity());

        Button okButton = (Button) questionnaire.getActivity().findViewById(R.id.debugOk);
        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "*** CLICK ***");
                questionnaire.setCurrentNode(getNextNode());
            }
        });

        ListView list = (ListView) questionnaire.getActivity().findViewById(R.id.valuePool);

        ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();
        for (String key : super.questionnaire.getSkemaValuePool().keySet()) {
            Variable<?> variable = super.questionnaire.getSkemaValuePool().get(key);
            Constant<?> expressionValue = variable.getExpressionValue();

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", key);
            map.put("type", typeAsString(expressionValue));
            map.put("value", valueAsString(expressionValue));
            mylist.add(map);
        }

        SimpleAdapter mSchedule = new SimpleAdapter(questionnaire.getActivity().getApplicationContext(), mylist,
                R.layout.row, new String[] { "name", "type", "value" }, new int[] { R.id.name, R.id.type, R.id.value });
        list.setAdapter(mSchedule);

        if (null == mylist || mylist.isEmpty()) {
            questionnaire.setCurrentNode(getNextNode());
        }
    }

    private void inflateView(Activity activity) {
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View monicaView = inflater.inflate(R.layout.debug, null);

        rootLayout.removeAllViews();
        rootLayout.addView(monicaView);
    }

    private String valueAsString(Constant<?> expressionValue) {
        if (expressionValue == null || expressionValue.getValue() == null)
            return "null";
        if (expressionValue.getType().endsWith("[]")) {
            StringBuffer sb = new StringBuffer("{ ");
            String end = " }";
            for (Object obj : (Object[]) expressionValue.getValue()) {
                String s = obj.toString();
                if (sb.length() + s.length() + 2 > 36) {
                    end = " ...";
                    break;
                }
                if (sb.length() > 2)
                    sb.append(", ");
                sb.append(s);
            }

            sb.append(end);
            return sb.toString();
        }

        String s = expressionValue.getValue().toString();
        if (s.length() > 40)
            s = s.subSequence(0, 36) + " ...";
        return s;
    }

    private String typeAsString(Constant<?> expressionValue) {
        if (expressionValue == null || expressionValue.getType() == null)
            return "null";
        String type = expressionValue.getType();
        if (type.endsWith("[]") && expressionValue.getValue() != null) {
            int length = ((Object[]) expressionValue.getValue()).length;
            type = type.replace("[]", "[" + length + "]");
        }
        return type;
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws UnknownVariableException {
        // Done..
    }
}
