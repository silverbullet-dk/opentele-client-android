package dk.silverbullet.telemed.questionnaire.output;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.utils.Util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    public void setOutput(Set<Variable<?>> output) {
        this.output = output;
    }

    public Set<Variable<?>> getOutput() {
        return output;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        QuestionnaireId = questionnaireId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }
}
