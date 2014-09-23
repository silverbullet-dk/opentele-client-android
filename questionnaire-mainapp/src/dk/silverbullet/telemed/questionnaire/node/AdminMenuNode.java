package dk.silverbullet.telemed.questionnaire.node;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.skema.SetAlarmSkema;
import dk.silverbullet.telemed.questionnaire.skema.SetServerIpSkema;
import dk.silverbullet.telemed.questionnaire.skema.SetShowUploadDebugNodeSkema;
import dk.silverbullet.telemed.utils.Util;

public class AdminMenuNode extends MenuNode  {

    private static final String TAG = Util.getTag(AdminMenuNode.class);

    private Node nextNode;

    private static String MENU_TEXT_EDIT_SERVER_URL;
    private static String MENU_TEXT_SHOW_UPLOAD_DEBUG;
    private static String MENU_TEXT_SET_ALARM;

    public AdminMenuNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);

        MENU_TEXT_EDIT_SERVER_URL = Util.getString(R.string.admin_change_server, questionnaire);
        MENU_TEXT_SHOW_UPLOAD_DEBUG = Util.getString(R.string.admin_show_upload_debug, questionnaire);
        MENU_TEXT_SET_ALARM = Util.getString(R.string.admin_set_alarm, questionnaire);
    }

    @Override
    public void enter() {
        hideMenuButton();
        hideBackButton();

        getQuestionnaire().clearStack();

        super.enter();
    }

    @Override
    public String toString() {
        return "AdminMenu(\"" + getNodeName() + "\") -> \"" + nextNode.getNodeName() + "\"";
    }

    @Override
    protected void createView() {
        Context context = questionnaire.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup rootLayout = questionnaire.getRootLayout();
        inflater.inflate(R.layout.admin_menu, rootLayout, true);

        linkTopPanel(rootLayout);

        if (!Util.isServerUrlLocked(questionnaire)) {
            showChangeSeverUrl(rootLayout);
        }

        rootLayout.findViewById(R.id.admin_menu_show_upload_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupAndRunSkema(new SetShowUploadDebugNodeSkema());
            }
        });

        rootLayout.findViewById(R.id.admin_menu_set_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupAndRunSkema(new SetAlarmSkema());
            }
        });

        rootLayout.findViewById(R.id.admin_menu_set_nonin_data_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupAndRunSkema(new ChangeNoninDataModeSkema());
            }
        });

    }

    private void showChangeSeverUrl(ViewGroup rootLayout) {
        TextView changeSeverUrl = (TextView) rootLayout.findViewById(R.id.admin_menu_change_server);
        changeSeverUrl.setVisibility(View.VISIBLE);
        changeSeverUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupAndRunSkema(new SetServerIpSkema());
            }
        });
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
