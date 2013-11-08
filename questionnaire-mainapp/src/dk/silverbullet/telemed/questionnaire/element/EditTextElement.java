package dk.silverbullet.telemed.questionnaire.element;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import java.util.Map;
import java.util.regex.Pattern;

import static dk.silverbullet.telemed.utils.Util.linkVariable;

public class EditTextElement extends Element {
    private static Pattern floatPattern = Pattern.compile("^\\d{1,6}(\\.\\d+)?");

    @Expose
    private Variable<?> outputVariable;

    private String popupTitle;
    private CharSequence[] popupItems;
    private String[] popupValues;

    private EditText editText;

    private boolean password;

    private boolean forMessageBody;

    private Integer decimals;

    public EditTextElement(final IONode node) {
        super(node);
    }

    public void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getQuestionnaire().getActivity());
        builder.setTitle(popupTitle);
        builder.setItems(popupItems, new DialogInterface.OnClickListener() {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public void onClick(DialogInterface dialog, int item) {
                editText.setText(popupItems[item]);
                outputVariable.setValue(new Constant(outputVariable.getType(), popupValues[item]));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setSelected(int item) {
        editText.setText(popupItems[item]);
        outputVariable.setValue(new Constant(outputVariable.getType(), popupValues[item]));
    }

    @Override
    public View getView() {
        if (editText == null) {
            editText = new EditText(getQuestionnaire().getActivity().getApplicationContext());

            if (password) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            } else if (forMessageBody) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }

            editText.setTextSize(TEXTSIZE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            editText.setLayoutParams(params);

            if (null != popupItems) {
                editText.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        showPopup();
                    }
                });

                editText.setInputType(InputType.TYPE_NULL);
                editText.setFocusableInTouchMode(false);

                boolean found = false;
                if (null != outputVariable && null != outputVariable.getExpressionValue()) {
                    for (int i = 0; i < popupValues.length; i++) {
                        if (popupValues[i].equals(outputVariable.getExpressionValue().getValue())) {
                            editText.setText(popupItems[i]);
                            found = true;
                        }
                    }
                    if (!found) {
                        editText.setText("");
                    }
                }
            } else {
                if (Number.class.isAssignableFrom(outputVariable.getType())) {
                    // No negative numbers are allowed, since currently no measurements in the system
                    // can be negative
                    boolean allowNegativeNumbers = false;
                    // Double and Floats allow decimal points, but not Integer
                    boolean allowDecimalPoint = !(Integer.class.isAssignableFrom(outputVariable.getType()));
                    editText.setKeyListener(new DigitsKeyListener(allowNegativeNumbers, allowDecimalPoint));
                }

                if (outputVariable.getExpressionValue() != null
                        && outputVariable.getExpressionValue().getValue() != null) {
                    editText.setText(outputVariable.getExpressionValue().toString());
                }
            }
        }
        return editText;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void leave() {
        String text = editText.getText().toString().trim();
        if (null == popupItems) {
            if (validates()) {
                outputVariable.setValue(new Constant(outputVariable.getType(), text));
            } else {
                outputVariable.setValue(new Constant(outputVariable.getType(), null));
            }
        }

        // Remove keyboard after use....
        InputMethodManager imm = (InputMethodManager) getQuestionnaire().getActivity().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        // Done
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        outputVariable = linkVariable(variablePool, outputVariable);
    }

    @Override
    public boolean validates() {
        if (editText == null || editText.getText() == null) {
            return false;
        }

        return validates(editText.getText().toString(), outputVariable.getType(), decimals);
    }

    public static boolean validates(String text, Class<?> clazz, Integer decimals) {
        String trimmedText = text.trim();

        if (trimmedText.equals("")) {
            return false;
        }

        if (Number.class.isAssignableFrom(clazz) && trimmedText.contains("-")) {
            return false;
        }

        if (Integer.class.isAssignableFrom(clazz) && trimmedText.contains(".")) {
            return false;
        }

        if (Integer.class.isAssignableFrom(clazz) && trimmedText.length() > 6) {
            return false;
        }

        if (decimals != null) {
            String[] textSplitBySeparator = trimmedText.split("\\.");
            boolean missesDecimalsBeforeComma = textSplitBySeparator[0].length() == 0;
            boolean hasTooManyDecimals = textSplitBySeparator.length == 2
                    && textSplitBySeparator[1].length() > decimals;
            if (missesDecimalsBeforeComma || hasTooManyDecimals) {
                return false;
            }
        }

        if (Float.class.isAssignableFrom(clazz) && !floatPattern.matcher(trimmedText).matches()) {
            return false;
        }

        return true;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public void setOutputVariable(Variable<?> outputVariable) {
        this.outputVariable = outputVariable;
    }

    public void setForMessageBody(boolean forMessageBody) {
        this.forMessageBody = forMessageBody;
    }
}
