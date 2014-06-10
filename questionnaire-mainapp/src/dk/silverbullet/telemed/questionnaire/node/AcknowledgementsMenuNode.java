package dk.silverbullet.telemed.questionnaire.node;

import android.app.ProgressDialog;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ListViewElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.rest.Resources;
import dk.silverbullet.telemed.rest.bean.acknowledgement.Acknowledgement;
import dk.silverbullet.telemed.rest.bean.acknowledgement.AcknowledgementListBean;
import dk.silverbullet.telemed.rest.listener.RetrieveEntityListener;
import dk.silverbullet.telemed.utils.Util;

import java.util.LinkedList;
import java.util.List;

public class AcknowledgementsMenuNode extends IONode implements RetrieveEntityListener<AcknowledgementListBean> {
    private ProgressDialog dialog;
    private Node nextNode;
    private List<Acknowledgement> acknowledgements = new LinkedList<Acknowledgement>();

    public AcknowledgementsMenuNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {

        hideBackButton();

        dialog = ProgressDialog.show(questionnaire.getContext(), Util.getString(R.string.acknowledgements_fetching, questionnaire), Util.getString(R.string.default_please_wait, questionnaire), true);

        Resources.getAcknowledgementList(questionnaire, this);

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
    public void retrieveError() {
        dialog.dismiss();
    }

    @Override
    public void retrieved(AcknowledgementListBean result) {
        acknowledgements = result.getAcknowledgements();

        buildView();
        createView();
        dialog.dismiss();
    }
}
