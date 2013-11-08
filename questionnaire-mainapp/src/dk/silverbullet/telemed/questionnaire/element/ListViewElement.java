package dk.silverbullet.telemed.questionnaire.element;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.utils.Util;

import java.util.*;

public class ListViewElement<T> extends Element {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(ListViewElement.class);

    @Expose
    private String next;

    private Node nextNode;

    @Expose
    private String[] values;

    @Expose
    private String[] valuesToHighlight;

    @Expose
    private T[] results;

    @Expose
    private Variable<T> variable;

    private ListView listView;

    private ArrayAdapter<String> listAdapter;

    private boolean real;

    public ListViewElement(final IONode node) {
        super(node);
    }

    @Override
    public View getView() {
        if (null == listView) {

            // Inflate our UI from its XML layout description.
            Activity activity = getQuestionnaire().getActivity();
            Context context = getQuestionnaire().getActivity().getApplicationContext();

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            listView = (ListView) inflater.inflate(R.layout.list_view_element, null);
            listAdapter = new HighlightingArrayAdapter(context, values, valuesToHighlight);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                    variable.setValue(results[position]);
                    // if (real) {
                    // getQuestionnaire().addSkemaVariable(new Variable<Object>(outputName, results[position]));
                    // } else {
                    // getQuestionnaire().addVariable(new Variable<Object>(outputName, results[position]));
                    // }

                    getQuestionnaire().setCurrentNode(nextNode);
                }
            });
        }

        return listView;
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        nextNode = map.get(next);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws UnknownVariableException {
        // Done1
    }

    @Override
    public boolean validates() {
        return true;
    }

    private static class HighlightingArrayAdapter extends ArrayAdapter<String> {
        private Set<String> valuesToHighlight;
        private String[] values;

        public HighlightingArrayAdapter(Context context, String[] values, String[] valuesToHighlight) {
            super(context, R.layout.menu_item, values);
            this.values = values;
            if (valuesToHighlight == null) {
                this.valuesToHighlight = Collections.emptySet();
            } else {
                this.valuesToHighlight = new HashSet<String>(Arrays.asList(valuesToHighlight));
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView result = (TextView) super.getView(position, convertView, parent);
            boolean highlighted = valuesToHighlight.contains(values[position]);
            int style = highlighted ? Typeface.BOLD : Typeface.NORMAL;
            // We cannot use result.getTypeface, since setting its style to Typeface.NORMAL will NOT
            // reset previously set bold style.
            result.setTypeface(null, style);
            return result;
        }
    }

    public void setResults(T[] results) {
        this.results = results;
    }

    public void setVariable(Variable<T> variable) {
        this.variable = variable;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setValuesToHighlight(String[] valuesToHighlight) {
        this.valuesToHighlight = valuesToHighlight;
    }
}
