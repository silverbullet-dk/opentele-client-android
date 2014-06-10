package dk.silverbullet.telemed.questionnaire.node;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class CRPNode extends DeviceNode {
    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(GlucoseUrineDeviceNode.class);

    @Expose
    private Variable<Integer> CRP;
    @Expose
    private String text;

    private ViewGroup buttonParent;

    private CheckBox underFiveCheckBox;

    private EditText resultEditText;

    public CRPNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        setView();
        super.enter();
    }

    public void setView() {
        Context context = questionnaire.getContext();
        inflateView(context);
        getViews();
        setInputValidation();
        linkTopPanel(questionnaire.getRootLayout());
    }

    private void setInputValidation() {
        resultEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                underFiveCheckBox.setChecked(resultTextIsUnderFive());
            }
        });

        underFiveCheckBox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && !resultTextIsUnderFive()) {
                    resultEditText.setText("");
                }
            }
        });
    }

    @Override
    protected void createView() {
    } // Overridden to avoid building the view via IONode

    private boolean resultTextIsUnderFive() {
        String resultString = resultEditText.getText().toString();
        if (resultString.isEmpty()) {
            return true;
        } else {
            long result = Integer.parseInt(resultString);
            return result < 5;
        }
    }

    private void inflateView(Context context) {
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View crpView = inflater.inflate(R.layout.crp_measurement, null);

        rootLayout.removeAllViews();
        rootLayout.addView(crpView);

        TextView headline = (TextView) rootLayout.findViewById(R.id.headline);
        headline.setText(text);

        buttonParent = (ViewGroup) rootLayout.findViewById(R.id.button_parent);
        TwoButtonElement be = new TwoButtonElement(this, Util.getString(R.string.default_omit, questionnaire), Util.getString(R.string.default_next, questionnaire));
        be.setLeftNextNode(getNextFailNode());
        be.setLeftSkipValidation(true);
        be.setRightNextNode(getNextNode());

        buttonParent.addView(be.getView());
    }

    public boolean validates() {
        return underFiveCheckBox.isChecked() || !resultEditText.getText().toString().isEmpty();
    }

    private void getViews() {
        ViewGroup rootLayout = questionnaire.getRootLayout();
        underFiveCheckBox = (CheckBox) rootLayout.findViewById(R.id.under_five);
        resultEditText = (EditText) rootLayout.findViewById(R.id.result);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        CRP = Util.linkVariable(variablePool, CRP);
    }

    @Override
    public void deviceLeave() {
        Constant<Integer> result;
        String resultString = resultEditText.getText().toString();
        if (resultTextIsUnderFive() || underFiveCheckBox.isChecked()) {
            result = new Constant<Integer>(0);
            CRP.setValue(result);
        } else if (!resultString.isEmpty()) {
            result = new Constant<Integer>(Integer.parseInt(resultString));
            CRP.setValue(result);
        }
    }
}
