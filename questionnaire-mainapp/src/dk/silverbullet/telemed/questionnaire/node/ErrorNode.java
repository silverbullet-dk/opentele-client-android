package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class ErrorNode extends IONode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(ErrorNode.class);

    private Node nextNode;
    private String error;

    private TextViewElement textViewElement;
    private ButtonElement button;

    public ErrorNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
        TextViewElement x = new TextViewElement(this);
        x.setText("Der er sket en fejl :(");
        addElement(x);

        textViewElement = new TextViewElement(this);
        addElement(textViewElement);
        button = new ButtonElement(this);
        button.setText("OK");
        addElement(button);
    }

    @Override
    public void enter() {
        textViewElement.setText("error:" + error);
        button.setNextNode(nextNode);
        super.enter();
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setError(String error) {
        this.error = error;
    }
}
