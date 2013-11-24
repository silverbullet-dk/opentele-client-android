package dk.silverbullet.telemed.rest.bean.acknowledgement;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.schedule.bean.QuestionnaireSchedule;

import java.util.List;

public class AcknowledgementListBean {
    @Expose private List<Acknowledgement> acknowledgements;

    public List<Acknowledgement> getAcknowledgements() {
        return acknowledgements;
    }
}


