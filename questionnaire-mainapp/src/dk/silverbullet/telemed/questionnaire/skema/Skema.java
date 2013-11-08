package dk.silverbullet.telemed.questionnaire.skema;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;

import java.util.*;

public class Skema {

    @Expose
    private String name;
    @Expose
    private String version;

    @Expose
    private String startNode;
    private Node startNodeNode;

    @Expose
    private String endNode;
    private EndNode endNodeNode;

    @Expose
    private List<Node> nodes;

    @Expose
    private Set<Variable<?>> output = new HashSet<Variable<?>>();

    public void addNode(Node node) {
        if (null == nodes)
            nodes = new ArrayList<Node>();

        nodes.add(node);
    }

    public void addVariable(Variable<?> output) {
        if (null == this.output)
            this.output = new HashSet<Variable<?>>();

        this.output.add(output);
    }

    public void link() throws UnknownNodeException {
        Map<String, Node> map = new HashMap<String, Node>();
        for (Node n : nodes) {
            map.put(n.getNodeName(), n);
        }

        for (Node n : nodes) {
            n.linkNodes(map);
        }

        endNodeNode = (EndNode) map.get(endNode);
        startNodeNode = map.get(startNode);
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        for (Node node : nodes) {
            node.setQuestionnaire(questionnaire);
            // if (node instanceof IONode) {
            // IONode ioNode = (IONode) node;
            // for (Element element : ioNode.getElements())
            // element.setNode(ioNode);
            // }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    public void setEndNode(String endNode) {
        this.endNode = endNode;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getEndNode() {
        return endNode;
    }

    public String getStartNode() {
        return startNode;
    }

    public Node getStartNodeNode() {
        return startNodeNode;
    }

    public EndNode getEndNodeNode() {
        return endNodeNode;
    }

    public Set<Variable<?>> getOutput() {
        return output;
    }
}
