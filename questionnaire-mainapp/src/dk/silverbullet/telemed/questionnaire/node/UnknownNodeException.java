package dk.silverbullet.telemed.questionnaire.node;

public class UnknownNodeException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1087207142569294937L;
    final String nodeName;

    public UnknownNodeException(String nodeName) {
        this.nodeName = nodeName;

    }

    @Override
    public String toString() {
        return "UnknownNode [nodeName= \"" + nodeName + "\"]";
    }

}
