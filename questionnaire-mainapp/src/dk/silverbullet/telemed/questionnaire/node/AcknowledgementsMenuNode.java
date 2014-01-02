package dk.silverbullet.telemed.questionnaire.node;

import android.app.ProgressDialog;
import dk.silverbullet.telemed.deleteme.TestOutputSkema;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ListViewElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.rest.RetrieveAcknowledgementListTask;
import dk.silverbullet.telemed.rest.RetrieveTask;
import dk.silverbullet.telemed.rest.bean.acknowledgement.Acknowledgement;
import dk.silverbullet.telemed.rest.bean.acknowledgement.AcknowledgementListBean;
import dk.silverbullet.telemed.rest.listener.ListListener;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

import java.util.LinkedList;
import java.util.List;

public class AcknowledgementsMenuNode extends IONode implements ListListener {

    private static final String TAG = Util.getTag(AcknowledgementsMenuNode.class);

    private ProgressDialog dialog;

    private Node nextNode;

    private List<Acknowledgement> acknowledgements = new LinkedList<Acknowledgement>();

    public AcknowledgementsMenuNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {

        hideBackButton();

        dialog = ProgressDialog.show(questionnaire.getActivity(), Util.getString(R.string.acknowledgements_fetching, questionnaire), Util.getString(R.string.default_please_wait, questionnaire), true);

        RetrieveTask asyncHttpPost = new RetrieveAcknowledgementListTask(questionnaire, this);
        asyncHttpPost.execute(TestOutputSkema.getOutputSkema());

        buildView();
        super.enter();
    }

    @Override
    public String toString() {
        return "AcknowledgementsMenuNode(\"" + getNodeName() + "\") -> \"" + nextNode.getNodeName() + "\"";
    }

    private void buildView() {

        clearElements();
        TextViewElement header = new TextViewElement(this);
        header.setText(Util.getString(R.string.acknowledgements_acknowledgements, questionnaire));
        addElement(header);

        ListViewElement<String> lve = new ListViewElement<String>(this);
        lve.setClickAhead(false);

        String[] acknowledgementArray = new String[acknowledgements.size()];
        for (int i = 0; i < acknowledgements.size(); i++) {
            acknowledgementArray[i] = acknowledgements.get(i).getMessage();
        }
        lve.setValues(acknowledgementArray);
        lve.setNextNode(nextNode);

        addElement(lve);
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public void sendError() {
        dialog.dismiss();
    }

    @Override
    public void setJson(String json) {

        AcknowledgementListBean listBean = Json.parse(json, AcknowledgementListBean.class);
        acknowledgements = listBean.getAcknowledgements();

        buildView();
        createView();
        dialog.dismiss();
    }
}
