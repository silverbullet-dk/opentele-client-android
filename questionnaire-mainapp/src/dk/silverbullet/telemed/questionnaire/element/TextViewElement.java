package dk.silverbullet.telemed.questionnaire.element;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;

import java.util.Map;

public class TextViewElement extends Element {

    @Expose
    private String text;

    @Expose
    private boolean header;

    private TextView textView;

    private int color;

    public TextViewElement(final IONode node) {
        super(node);
    }

    public TextViewElement(final IONode node, String text, boolean header) {
        this(node);
        setText(text);
    }

    public TextViewElement(final IONode node, String text) {
        this(node, text, false);
    }

    @Override
    public View getView() {
        if (null == textView) {
            Context context = getQuestionnaire().getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (header)
                textView = (TextView) inflater.inflate(R.layout.text_header_element, null);
            else
                textView = (TextView) inflater.inflate(R.layout.text_element, null);

            textView.setText(text);

            // TODO Change this to a condition. E.g. Error-text, normal-text
            if (Color.TRANSPARENT != color)
                textView.setTextColor(color);
        }

        return textView;
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws UnknownVariableException {
        // Done!
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
        if (null != textView) {
            textView.setText(text);
        }
    }

    @Override
    public boolean validates() {
        return true;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }
}
