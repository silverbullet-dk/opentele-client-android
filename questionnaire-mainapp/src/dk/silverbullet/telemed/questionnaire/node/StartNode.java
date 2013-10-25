package dk.silverbullet.telemed.questionnaire.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import dk.silverbullet.telemed.questionnaire.Questionnaire;

@Data
@EqualsAndHashCode(callSuper = false)
public class StartNode extends IONode {

    private Node next;

    public StartNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        super.enter();
        questionnaire.setCurrentNode(next);
    }
}
