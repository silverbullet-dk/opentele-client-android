package dk.silverbullet.telemed.questionnaire.output;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.utils.Util;

/**
 * https://teleskejby-devel.silverbullet.dk/teleskejby-server/questionnaire/upload
 */
@Data
public class OutputSkema {

    @Expose
    private String name;
    @Expose
    private String version;
    @Expose
    private String date;
    @Expose
    private Set<Variable<?>> output = new HashSet<Variable<?>>();
    @Expose
    private String PatientId;
    @Expose
    private Long QuestionnaireId;

    public void setDate(Date date) {
        this.date = Util.ISO8601_DATE_TIME_FORMAT.format(date);
    }

    public void addVariable(Variable<?> output) {
        this.output.add(output);
    }

    public Variable<?> getVariable(String name) {
        Variable<?> result = null;
        for (Variable<?> o : output)
            if (o.getName().equals(name))
                result = o;

        return result;
    }
}
