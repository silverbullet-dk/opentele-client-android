package dk.silverbullet.telemed.questionnaire.node;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;

public class SetNoninDataModeNode extends IONode {
    private enum Dataformats {
        DATAFORMAT_8,
        DATAFORMAT_13
    }

    public SetNoninDataModeNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        setView();
        super.enter();

    }

    private void setView() {
        clearElements();

        Context context = questionnaire.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup rootLayout = questionnaire.getRootLayout();
        View parentView = inflater.inflate(R.layout.admin_set_nonin_dataformat, rootLayout, true);

        parentView.findViewById(R.id.admin_menu_nonin_set_dataformat_13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDataFormat(Dataformats.DATAFORMAT_13);
            }
        });

        parentView.findViewById(R.id.admin_menu_nonin_set_dataformat_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDataFormat(Dataformats.DATAFORMAT_8);
            }
        });

    }

    private void setDataFormat(Dataformats dataformat) {
        Util.showToast(questionnaire, questionnaire.getContext().getString(R.string.admin_set_nonin_data_mode_setting));
        try {
            switch (dataformat) {
                case DATAFORMAT_13:
                    new NoninDataModeChanger(this, true);
                    break;
                case DATAFORMAT_8:
                    new NoninDataModeChanger(this, false);
                    break;
            }
        } catch (DeviceInitialisationException e) {
            OpenTeleApplication.instance().logException(e);
            e.printStackTrace();
        }
    }

    public void dataformatChanged() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Util.showToast(questionnaire, questionnaire.getContext().getString(R.string.admin_set_nonin_data_mode_set));
            }
        });
    }
}
