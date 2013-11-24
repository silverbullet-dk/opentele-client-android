package dk.silverbullet.telemed.video.measurement;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.video.VideoActivity;
import dk.silverbullet.telemed.video.measurement.adapters.*;

public class TakeMeasurementFragment extends Fragment implements MeasurementInformer {
    private MeasurementType currentMeasurementType;
    private TextView statusText;
    VideoMeasurementAdapter measurementAdapter;
    private TextView measurementTypeText;
    private PendingMeasurementPoller pendingMeasurementPoller;
    private ViewGroup takeMeasurementViewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View takeMeasurementView = inflater.inflate(R.layout.video_take_measurement, container, false);

        takeMeasurementViewGroup = (ViewGroup) takeMeasurementView.findViewById(R.id.take_measurement_parent);
        measurementTypeText = (TextView) takeMeasurementView.findViewById(R.id.measurement_type);
        statusText = (TextView) takeMeasurementView.findViewById(R.id.status_text);

        return takeMeasurementView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pendingMeasurementPoller = new PendingMeasurementPoller(this);
        pendingMeasurementPoller.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(measurementAdapter != null) {
            measurementAdapter.close();
        }

        pendingMeasurementPoller.stop();
    }

    public void takeMeasurement(PendingMeasurement pendingMeasurement) {
        MeasurementType newMeasurementType = pendingMeasurement == null ? null : pendingMeasurement.type;

        boolean shouldStartMeasuring = currentMeasurementType == null && newMeasurementType != null;
        boolean shouldStopMeasuring = newMeasurementType != currentMeasurementType;

        if (shouldStartMeasuring) {
            reveal();
            measurementAdapter = createVideoMeasurementAdapter(pendingMeasurement);
            measurementAdapter.start();
            currentMeasurementType = newMeasurementType;
        } else if (shouldStopMeasuring) {
            hide();
            if (measurementAdapter != null) {
                measurementAdapter.close();
                measurementAdapter = null;
            }
            currentMeasurementType = null;
        }
    }

    @Override
    public void reveal() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                takeMeasurementViewGroup.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hide() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                takeMeasurementViewGroup.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setStatusText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
            }
        });
    }

    @Override
    public void setMeasurementTypeText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                measurementTypeText.setText(text);
            }
        });
    }

    @Override
    public String getClientVersion() {
        return getVideoActivity().getString(R.string.client_version);
    }

    @Override
    public String getUsername() {
        return getVideoActivity().getUsername();
    }

    @Override
    public String getPassword() {
        return getVideoActivity().getPassword();
    }

    @Override
    public String getServerUrl() {
        return getVideoActivity().getServerURL();
    }

    private VideoMeasurementAdapter createVideoMeasurementAdapter(PendingMeasurement pendingMeasurement) {
        switch (pendingMeasurement.type) {
            case LUNG_FUNCTION:
                return new LungMeasurementAdapter(this, getActivity());
            case BLOOD_PRESSURE:
                return new BloodPressureAdapter(this);
            case SATURATION:
                return new SaturationMeasurementAdapter(this);
            default:
                throw new IllegalArgumentException("Unknown measurement type: '" + pendingMeasurement.type + "'");
        }
    }

    private VideoActivity getVideoActivity() {
        return (VideoActivity) getActivity();
    }
}
