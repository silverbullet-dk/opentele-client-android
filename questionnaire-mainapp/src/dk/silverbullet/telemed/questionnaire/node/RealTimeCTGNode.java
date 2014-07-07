package dk.silverbullet.telemed.questionnaire.node;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import dk.silverbullet.telemed.device.AmbiguousDeviceException;
import dk.silverbullet.telemed.device.BluetoothDisabledException;
import dk.silverbullet.telemed.device.BluetoothNotAvailableException;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.monica.MonicaDeviceController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.node.monica.DeviceState;
import dk.silverbullet.telemed.questionnaire.node.monica.MonicaDeviceCallback;
import dk.silverbullet.telemed.questionnaire.node.monica.SimulatedMonicaDevice;
import dk.silverbullet.telemed.utils.Util;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RealTimeCTGNode extends IONode implements MonicaDeviceCallback {

    private static final String TAG = Util.getTag(RealTimeCTGNode.class);
    private MonicaDeviceController device;
    private CheckBox buttonOrange;
    private CheckBox buttonWhite;
    private CheckBox buttonGreen;
    private CheckBox buttonBlack;
    private CheckBox buttonYellow;

    private BlockingQueue<RealTimeCTGMessage> measurementsQueue = new ArrayBlockingQueue<RealTimeCTGMessage>(50000);
    private RealtimeCTGMeasurementsExportWorker worker;
    private int sampleCount = 0;
    private boolean simulate = false;
    private DeviceState currentState;
    private TextView statusTextView;


    public RealTimeCTGNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        inflateLayout();

        ViewGroup rootLayout = questionnaire.getRootLayout();

        linkTopPanel(rootLayout);

        PatientInfo patientInfo = new PatientInfo();
        patientInfo.id = questionnaire.getUserId();

        String name = questionnaire.getFullName();

        int lastNameStartIndex = name.lastIndexOf(" ");
        patientInfo.lastName = name.substring(lastNameStartIndex, name.length());
        patientInfo.firstName = name.substring(0, lastNameStartIndex);


        worker = new RealtimeCTGMeasurementsExportWorker(questionnaire.getActivity(), measurementsQueue, patientInfo);
        worker.start();
        super.enter();
    }

    private ProgressDialog dialog;
    private WebView webView;

    private void inflateLayout() {
        Context context = questionnaire.getContext();
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View topView = inflater.inflate(R.layout.realtime_ctg_node, null);

        statusTextView = (TextView) topView.findViewById(R.id.realtime_ctg_current_status);

        rootLayout.removeAllViews();
        rootLayout.addView(topView);

        topView.findViewById(R.id.realtime_ctg_start_measurement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeasurement();
            }
        });

        topView.findViewById(R.id.realtime_ctg_stop_measurement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMeasurement();
            }
        });

        resolveViews(topView);

    }

    private void resolveViews(View parentView) {
        buttonOrange = (CheckBox) parentView.findViewById(R.id.buttonOrange);
        buttonWhite = (CheckBox) parentView.findViewById(R.id.buttonWhite);
        buttonGreen = (CheckBox) parentView.findViewById(R.id.buttonGreen);
        buttonBlack = (CheckBox) parentView.findViewById(R.id.buttonBlack);
        buttonYellow = (CheckBox) parentView.findViewById(R.id.buttonYellow);
    }

    private void startMeasurement() {
        if(simulate) {
            new SimulatedMonicaDevice(this);
        } else {
            try {
                device = new MonicaDeviceController(this, questionnaire.getContext());
            } catch (BluetoothDisabledException e) {
                Toast.makeText(questionnaire.getContext().getApplicationContext(), Util.getString(R.string.monica_bluetooth_off, questionnaire),Toast.LENGTH_LONG).show();
            } catch (BluetoothNotAvailableException e) {
                Toast.makeText(questionnaire.getContext().getApplicationContext(), Util.getString(R.string.monica_bluetooth_unavaliable, questionnaire), Toast.LENGTH_LONG).show();
            } catch (AmbiguousDeviceException e) {
                Toast.makeText(questionnaire.getContext().getApplicationContext(),Util.getString(R.string.monica_multiple_devices, questionnaire), Toast.LENGTH_LONG).show();
            } catch (DeviceInitialisationException e) {
                Toast.makeText(questionnaire.getContext().getApplicationContext(), Util.getString(R.string.monica_failed_to_start, questionnaire),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void stopMeasurement() {
        device.close();
    }


    @Override
    public void addSamples(float[] mhr, float[] fhr, int[] qfhr, float[] toco) {
        Log.d(TAG, "Got sample");
        sampleCount++;
        boolean didAddMessage = measurementsQueue.offer(new SampleMessage(mhr, fhr, qfhr, toco, sampleCount));

        if(!didAddMessage) {
            Log.w(TAG, "Could not add measurement to queue");
        }
    }

    @Override
    public void updateProgress(int i, int samples) {
        //Not relevant for real-time measurements
    }

    @Override
    public void setProbeState(final boolean orange, final boolean white, final boolean green, final boolean black, final boolean yellow) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonOrange.setChecked(orange);
                buttonWhite.setChecked(white);
                buttonGreen.setChecked(green);
                buttonBlack.setChecked(black);
                buttonYellow.setChecked(yellow);
            }
        });
    }

    @Override
    public void setState(DeviceState state) {
        if (currentState == state) {
            return;
        }

        currentState = state;
        final String text;

        switch (currentState) {
            case WAITING_FOR_CONNECTION:
                text = Util.getString(R.string.monica_waiting_for_connection, questionnaire);
                break;
            case CHECKING_STARTING_CONDITION:
                text = Util.getString(R.string.monica_checking_connection, questionnaire);
                break;
            case WAITING_FOR_DATA:
                text = Util.getString(R.string.monica_waiting_for_data, questionnaire);
                break;
            case RECEIVING_DATA:
                text = Util.getString(R.string.monica_receiving_data, questionnaire);
                break;
            case CLOSING:
            case PROCESSING_DATA:
                text = Util.getString(R.string.monica_closing, questionnaire);
                break;
            default:
                text = "";
        }

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(text);
            }
        });
    }

    @Override
    public void abort(String string) {
    }

    @Override
    public void done(String message) {
    }

    @Override
    public void done() {
    }

    @Override
    public void setStartVoltage(float voltage) {
    }

    @Override
    public void setEndVoltage(float voltage) {
    }

    @Override
    public void setStartTimeValue(Date dateTime) {
    }

    @Override
    public void setEndTimeValue(Date dateTime) {
    }

    @Override
    public void addSignal(Date dateTime) {
        measurementsQueue.add(new SignalMessage(dateTime));
    }

    @Override
    public void setDeviceIdString(String deviceId) {
    }

    @Override
    public int getSampleTimeMinutes() {
        return 43200; //One month should be sufficient
    }

    @Override
    public Date getStartTimeValue() {
        return null;
    }

    @Override
    public void addFetalHeight(int fetalHeight) {
        Log.d(TAG, "Got fetal height");
    }

    @Override
    public void addSignalToNoise(int signalToNoise) {
        Log.d(TAG, "Got signal to noise rato");
    }
}
