package dk.silverbullet.telemed.questionnaire;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.WebViewNode;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Util;

import java.util.*;

public class Questionnaire {
    private static final String TAG = Util.getTag(Questionnaire.class);

    private boolean running;

    private Node startNode;
    private Node pastNode;


    private Node currentNode;

    private final Map<String, Variable<?>> valuePool = new HashMap<String, Variable<?>>();

    private final Map<String, Variable<?>> skemaValuePool = new HashMap<String, Variable<?>>();

    private OutputSkema outputSkema;

    public void cleanSkemaValuePool() {
        skemaValuePool.clear();
    }

    private Node previousNode;

    private boolean backPressed;

    private Stack<IONode> ioNodeStack = new Stack<IONode>();

    private final Stack<Map<String, Constant<?>>> varStack = new Stack<Map<String, Constant<?>>>();

    private final QuestionnaireFragment parentFragment;

    private Activity activity;

    public Questionnaire(QuestionnaireFragment parentFragment) {
        this.parentFragment = parentFragment;
        this.activity = parentFragment.getActivity();
    }

    public void addVariable(Variable<?> output) {
        this.valuePool.put(output.getName(), output);
    }

    public void addSkemaVariable(Variable<?> output) {
        this.skemaValuePool.put(output.getName(), output);
    }

    public void execute() {
        try {
            int count = 0;
            running = true;
            long t0 = System.currentTimeMillis();
            while (currentNode != null) {
                count++;
                if (count > 1000000) {
                    throw new RuntimeException("LOOP COUNT EXCEEDED!");
                }
                previousNode = currentNode;
                currentNode = null;
                previousNode.enter();
            }
            long t1 = System.currentTimeMillis();
            Log.d(TAG, "Loop count: " + count + " in " + (t1 - t0));
        } finally {
            running = false;
        }
    }

    public void setCurrentNode(Node newNode) {
        if (null == newNode) {
            Toast.makeText(getActivity().getApplicationContext(), "NextNode is undefined", Toast.LENGTH_SHORT).show();
            return;
        }

        if (previousNode != null) {
            previousNode.leave();
        }

        currentNode = newNode;
        if (!running) {
            execute();
        }
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public void start() {
        Log.d(TAG, "start...");
        setCurrentNode(startNode);
        execute();
    }

    public void back() {
        backPressed = true;
        if (getCurrentNode() == null) {
            if (!ioNodeStack.isEmpty()) {

                // Handle the special case of WebViewNodes which, trough a webview, have an internal stack. If that
                // stack is nonempty, pop it. Otherwise proceed as normal
                if (stackTopIsWebViewNode()) {
                    WebViewNode webViewNode = (WebViewNode) ioNodeStack.peek();
                    if (webViewNode.canGoBack()) {
                        webViewNode.goBack();
                        backPressed = false;
                        return;
                    }
                }

                IONode popped = ioNodeStack.pop();
                varStack.pop();
                Log.d(TAG, "* Popped: " + popped.getNodeName());
                if (!ioNodeStack.isEmpty()) {
                    IONode peeked = ioNodeStack.peek();
                    Log.d(TAG, "* Peeked: " + peeked.getNodeName());
                }
            }
            goBack();
        }
    }

    private boolean stackTopIsWebViewNode() {
        return ioNodeStack.peek().getClass() == WebViewNode.class;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void goBack() {
        Log.d(TAG, "goBack! stack size:" + ioNodeStack.size());
        backPressed = false;
        if (ioNodeStack.isEmpty()) {
            Log.d(TAG, "Empty stack - terminating!");
            parentFragment.getActivity().finish();
        } else {
            Log.d(TAG, "Going back!");
            Set<String> vars = new HashSet<String>(skemaValuePool.keySet());
            Map<String, Constant<?>> temp = varStack.pop();

            IONode popped = ioNodeStack.pop();
            if (!ioNodeStack.isEmpty()) {
                Log.d(TAG, "next: " + ioNodeStack.peek().getNodeName());
            }
            setCurrentNode(popped);

            for (String name : vars) {
                if (temp.containsKey(name)) {
                    Log.d(TAG, "***** " + name + "=" + temp.get(name));
                    skemaValuePool.get(name).setValue((Constant) temp.get(name));
                } else {
                    Log.d(TAG, "***** " + name + " DELETED");
                    skemaValuePool.remove(name);
                }
            }
        }
    }

    public void clearStack() {
        ioNodeStack.clear();
        varStack.clear();
    }

    @SuppressWarnings("unchecked")
    public void logout() {
        Variable<Boolean> isLoggedIn = (Variable<Boolean>) getValuePool().get(Util.VARIABLE_IS_LOGGED_IN);
        isLoggedIn.setValue(false);

        Variable<Boolean> isLoggedInAsAdmin = (Variable<Boolean>) getValuePool().get(Util.VARIABLE_IS_LOGGED_IN_AS_ADMIN);
        isLoggedInAsAdmin.setValue(false);

        Variable<String> userName = (Variable<String>) getValuePool().get(Util.VARIABLE_USERNAME);
        userName.setValue("");

        Variable<String> password = (Variable<String>) getValuePool().get(Util.VARIABLE_PASSWORD);
        password.setValue("");

        MainQuestionnaire.getInstance().adviceActivityOfUserLogout();

        setCurrentNode(MainQuestionnaire.getInstance().getMainMenu());
    }

    public boolean isIONodeStackEmpty() {
        return ioNodeStack.isEmpty();
    }

    public void push(IONode ioNode) {
        for (int i = 0; i < ioNodeStack.size(); i++) {
            if (ioNodeStack.get(i) == ioNode) {
                ioNodeStack.setSize(i + 1);
                varStack.setSize(i + 1);
                return;
            }
        }

        ioNodeStack.push(ioNode);
        // Capture all variables:
        Map<String, Constant<?>> temp = new HashMap<String, Constant<?>>();
        for (String name : skemaValuePool.keySet()) {
            Log.d(TAG, "##### " + name + ": " + skemaValuePool.get(name).getExpressionValue());
            temp.put(name, skemaValuePool.get(name).getExpressionValue());
        }
        varStack.push(temp);
    }

    public void chainToNextIONode() {
        // Make current IONode "Invisible" in the navigation history
        if (!isIONodeStackEmpty()) {
            ioNodeStack.pop();
            varStack.pop();
        }
    }

    public String getFullName() {
        if (null != valuePool && valuePool.containsKey(Util.VARIABLE_REAL_NAME)) {
            return valuePool.get(Util.VARIABLE_REAL_NAME).getExpressionValue().toString();
        }
        return "";
    }

    public Long getUserId() {
        if (null != valuePool && valuePool.containsKey(Util.VARIABLE_USER_ID)) {
            return (Long) valuePool.get(Util.VARIABLE_USER_ID).getExpressionValue().getValue();
        }
        return null;
    }

    public ViewGroup getRootLayout() {
        return parentFragment.getRootLayout();
    }

    public Activity getActivity() {
        return activity;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Map<String, Variable<?>> getValuePool() {
        return valuePool;
    }

    public boolean isBackPressed() {
        return backPressed;
    }

    public void setOutputSkema(OutputSkema outputSkema) {
        this.outputSkema = outputSkema;
    }

    public Map<String, Variable<?>> getSkemaValuePool() {
        return skemaValuePool;
    }

    public OutputSkema getOutputSkema() {
        return outputSkema;
    }
}