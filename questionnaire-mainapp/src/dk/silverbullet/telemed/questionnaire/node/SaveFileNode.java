package dk.silverbullet.telemed.questionnaire.node;

import android.util.Log;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

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
        TextViewElement tve = new TextViewElement(this, Util.getString(R.string.save_file_data_saved_locally, questionnaire));
        addElement(tve);

        File outFile = new File("/mnt/sdcard/Download/data.jsn");
        statusText = new TextViewElement(this, Util.getString(R.string.save_file_save_to, questionnaire) + outFile.getAbsolutePath());

        addElement(statusText);
        addElement(new ButtonElement(this, Util.getString(R.string.default_ok, questionnaire), nextNode));

        try {
            String json = getJson();
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(json.getBytes("UTF-8"));
            os.close();
            statusText.setText(Util.getString(R.string.save_file_saved_in, questionnaire) + outFile.getAbsolutePath());
        } catch (IOException ex) {
            statusText.setText(Util.getString(R.string.save_file_error, questionnaire) + ex);
            Log.w(TAG, ex);
        }

        super.enter();
    }

    public String getJson() throws IOException {
        Map<String, Variable<?>> out = questionnaire.getSkemaValuePool();
        if (null == out) {
            throw new IOException("questionnaire.getSkemaValuePool() == null");
        }

        String json = Json.print(out);

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

    public void setNext(String next) {
        this.next = next;
    }
}
