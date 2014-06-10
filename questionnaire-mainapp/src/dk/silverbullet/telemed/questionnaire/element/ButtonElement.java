package dk.silverbullet.telemed.questionnaire.element;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class ButtonElement extends Element {

    private static final String TAG = Util.getTag(ButtonElement.class);

    public static final String GRAVITY_RIGHT = "right";
    public static final String GRAVITY_LEFT = "left";
    public static final String GRAVITY_CENTER = "center";

    @Expose
    private boolean skipValidation;
    private String validateText;

    @Expose
    private String text;

    @Expose
    private String next;

    private Node nextNode;

    @Expose
    private String gravity;

    private Button button;

    private LinearLayout layout;

    public ButtonElement(final IONode node) {
        super(node);
    }

    public ButtonElement(final IONode node, String text) {
        this(node);
        setText(text);
    }

    public ButtonElement(IONode node, String text, Node nextNode) {
        this(node, text);
        this.nextNode = nextNode;
    }

    @Override
    public View getView() {
        if (layout == null) {

            // Inflate our UI from its XML layout description.
            Context context = getQuestionnaire().getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflater.inflate(R.layout.button_element, null);

            button = (Button) layout.findViewById(R.id.button);

            layout.removeAllViews();
            button.setText(text);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "*CLICK BUTTON*");
                    if (skipValidation || node.validates())
                        getQuestionnaire().setCurrentNode(nextNode);
                    else {
                        String text = "Et eller flere felter er ikke udfyldt korrekt";
                        if (null != validateText && !"".equals(validateText.trim()))
                            text = validateText;

                        Util.showToast(getQuestionnaire(), text);
                    }
                }
            });
            button.setTextSize(TEXTSIZE);

            if (null != gravity && GRAVITY_RIGHT.equalsIgnoreCase(gravity)) {
                layout.setGravity(Gravity.RIGHT);
            } else if (null != gravity && GRAVITY_LEFT.equalsIgnoreCase(gravity)) {
                layout.setGravity(Gravity.LEFT);
            } else {
                layout.setGravity(Gravity.CENTER);
            }

            layout.addView(button);
        }

        return layout;
    }

    public void setText(String text) {
        this.text = text;
        if (null != button)
            button.setText(text);
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        nextNode = map.get(next);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws UnknownVariableException {
        // Done!
    }

    @Override
    public boolean validates() {
        return true;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }
}
