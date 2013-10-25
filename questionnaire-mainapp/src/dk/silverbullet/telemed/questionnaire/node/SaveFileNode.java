package dk.silverbullet.telemed.questionnaire.node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import android.util.Log;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "nextNode")
public class SaveFileNode extends IONode {
    private static final String TAG = Util.getTag(SaveFileNode.class);

    @Expose
    private String next;

    private TextViewElement statusText;

    private Node nextNode;

    public SaveFileNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        Log.d(TAG, "SaveFileNode Enter()");
        TextViewElement tve = new TextViewElement(this, "Data gemmes lokalt");
        addElement(tve);

        File outFile = new File("/mnt/sdcard/Download/data.jsn");
        statusText = new TextViewElement(this, "Saving to: " + outFile.getAbsolutePath());

        addElement(statusText);
        addElement(new ButtonElement(this, "OK", nextNode));

        try {
            String json = getJson();
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(json.getBytes("UTF-8"));
            os.close();
            statusText.setText("Data gemt i " + outFile.getAbsolutePath());
        } catch (IOException ex) {
            statusText.setText("Fejl: " + ex);
            Log.w(TAG, ex);
        }

        super.enter();
    }

    public String getJson() throws IOException {
        Map<String, Variable<?>> out = questionnaire.getSkemaValuePool();
        if (null == out) {
            throw new IOException("questionnaire.getSkemaValuePool() == null");
        }

        String json = Util.getGsonForOutput().toJson(out);

        if (json == null) {
            throw new IOException("..toJson() == null");
        }

        return json;
    }

    @Override
    public void leave() {
        // Nothing to do
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        nextNode = map.get(next);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws UnknownVariableException {
        // Nothing to do
    }
}
