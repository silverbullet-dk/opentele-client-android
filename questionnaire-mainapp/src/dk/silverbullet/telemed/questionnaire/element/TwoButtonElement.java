package dk.silverbullet.telemed.questionnaire.element;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
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

@Data
@EqualsAndHashCode(callSuper = false)
public class TwoButtonElement extends Element {

    private static final String TAG = Util.getTag(TwoButtonElement.class);

    @Expose
    private String leftText;
    @Expose
    private String leftNext;
    private Node leftNextNode;

    @Expose
    private String rightText;
    @Expose
    private String rightNext;
    private Node rightNextNode;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private Button leftButton;
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private Button rightButton;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private LinearLayout layout;

    @Expose
    private boolean rightSkipValidation;
    @Expose
    private boolean leftSkipValidation;

    private boolean hideRightButton;

    public TwoButtonElement(final IONode node) {
        super(node);
    }

    public TwoButtonElement(IONode node, String left, String right) {
        this(node);
        setLeftText(left);
        setRightText(right);
    }

    @Override
    public View getView() {
        if (null == layout) {

            // Inflate our UI from its XML layout description.
            Activity activity = getQuestionnaire().getActivity();
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflater.inflate(R.layout.two_button_element, null);
            leftButton = (Button) layout.findViewById(R.id.left);
            leftButton.setText(leftText);
            leftButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "*CLICK LEFT*");
                    if (leftSkipValidation || node.validates())
                        getQuestionnaire().setCurrentNode(leftNextNode);
                    else {
                        String text = "Et eller flere felter er ikke udfyldt korrekt";
                        Util.showToast(getQuestionnaire(), text);
                    }
                }
            });
            leftButton.setTextSize(TEXTSIZE);

            rightButton = (Button) layout.findViewById(R.id.right);
            rightButton.setText(rightText);
            rightButton.setVisibility(hideRightButton ? View.GONE : View.VISIBLE);
            rightButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "*CLICK RIGHT*");
                    if (rightSkipValidation || node.validates())
                        getQuestionnaire().setCurrentNode(rightNextNode);
                    else {
                        String text = "Et eller flere felter er ikke udfyldt korrekt";
                        Util.showToast(getQuestionnaire(), text);
                    }
                }
            });
            rightButton.setTextSize(TEXTSIZE);
        }

        return layout;
    }

    public void setLeftText(String text) {
        this.leftText = text;

        if (null != leftButton) {
            leftButton.setText(text);
            if (null == text)
                leftButton.setVisibility(Button.INVISIBLE);
            else
                leftButton.setVisibility(Button.VISIBLE);
        }
    }

    public void setRightText(String text) {
        this.rightText = text;
        if (null != rightButton) {
            rightButton.setText(text);
            if (null == text)
                rightButton.setVisibility(Button.INVISIBLE);
            else
                rightButton.setVisibility(Button.VISIBLE);
        }
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        leftNextNode = map.get(leftNext);
        rightNextNode = map.get(rightNext);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws UnknownVariableException {
        // Done1
    }

    @Override
    public boolean validates() {
        return true;
    }

    public void hideRightButton() {
        hideRightButton = true;
        if (rightButton != null) {
            rightButton.setVisibility(View.GONE);
        }
    }

    public void showRightButton() {
        hideRightButton = false;
        if (rightButton != null) {
            rightButton.setVisibility(View.VISIBLE);
        }
    }
}
