package dk.silverbullet.telemed.questionnaire.element;

import lombok.Data;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Expression;

@Data
public class ValueChoice<T> {
    @Expose
    Expression<T> value;

    @Expose
    String text;

    public void setValue(T value) {
        this.value = new Constant<T>(value);
    }

    public ValueChoice(T value, String text) {
        setValue(value);
        setText(text);
    }

}
