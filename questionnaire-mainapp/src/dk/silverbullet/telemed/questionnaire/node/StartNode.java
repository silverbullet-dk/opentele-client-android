package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.questionnaire.Questionnaire;

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

    public void setNext(Node next) {
        this.next = next;
    }
}
