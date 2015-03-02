package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.device.AmbiguousDeviceException;
import dk.silverbullet.telemed.device.BluetoothDisabledException;
import dk.silverbullet.telemed.device.BluetoothNotAvailableException;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.monica.MonicaDeviceController;
import dk.silverbullet.telemed.questionnaire.MainQuestionnaire;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.monica.DeviceState;
import dk.silverbullet.telemed.questionnaire.node.monica.MonicaDeviceCallback;
import dk.silverbullet.telemed.questionnaire.node.monica.SimulatedMonicaDevice;
import dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators.CommunicatorFactory;
import dk.silverbullet.telemed.utils.Util;

import java.util.Date;
import java.util.UUID;
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
    private String deviceName;
    private SimulatedMonicaDevice simulatedDevice;
    private ProgressDialog progress;
    private boolean isStopping, hasStopped;
    private StopMeasurementsAsyncTask waitForEmptyQueue;
    private PatientInfo patientInfo;
    private UUID registrationIdentifier;


    public RealTimeCTGNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        inflateLayout();

        ViewGroup rootLayout = questionnaire.getRootLayout();

        linkTopPanel(rootLayout);

        hideBackButton();
        super.enter();
    }

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

        questionnaire.getActivity().findViewById(R.id.realtime_ctg_stop_measurement).setVisibility(View.VISIBLE);
        questionnaire.getActivity().findViewById(R.id.realtime_ctg_start_measurement).setVisibility(View.INVISIBLE);

        initializeDevice();
        setupMeasurementExportWorker();
    }

    private void initializeDevice() {

        if(simulate) {
            simulatedDevice = new SimulatedMonicaDevice(this);
            this.deviceName = "silverbullet-monica-test";
        } else {
            try {
                device = new MonicaDeviceController(this, questionnaire.getContext());
                this.deviceName = device.getDeviceName();
            } catch (BluetoothDisabledException e) {
                OpenTeleApplication.instance().logException(e);
                abort(Util.getString(R.string.realtime_monica_bluetooth_off, questionnaire));
            } catch (BluetoothNotAvailableException e) {
                OpenTeleApplication.instance().logException(e);
                abort(Util.getString(R.string.realtime_monica_bluetooth_unavaliable, questionnaire));
            } catch (AmbiguousDeviceException e) {
                OpenTeleApplication.instance().logException(e);
                abort(Util.getString(R.string.realtime_monica_multiple_devices, questionnaire));
            } catch (DeviceInitialisationException e) {
                OpenTeleApplication.instance().logException(e);
                abort(Util.getString(R.string.realtime_monica_failed_to_start, questionnaire));
            }
        }
    }

    private void setupMeasurementExportWorker() {
        patientInfo = new PatientInfo();
        patientInfo.id = questionnaire.getUserId();
        String name = questionnaire.getFullName();

        int lastNameStartIndex = name.lastIndexOf(" ");
        patientInfo.lastName = name.substring(lastNameStartIndex, name.length());
        patientInfo.firstName = name.substring(0, lastNameStartIndex);

        registrationIdentifier = UUID.randomUUID();

        new setupWorkerAsyncTask().execute();

    }

    private void stopMeasurement() {

        this.questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(Util.getString(R.string.realtime_ctg_stopping, questionnaire));
            }
        });

        //Show dialog
        showStoppingDialog();
        isStopping = true;
        waitForEmptyQueue = new StopMeasurementsAsyncTask();
        waitForEmptyQueue.execute();
    }


    private class StopMeasurementsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            isStopping = true;
            closeDevice();

            measurementsQueue.add(new StopMessage()); //Tells the worker thread to stop working when it gets to this message


            while(measurementsQueue.size() > 0 && worker.isRunning()) {
                Log.d(TAG, "waiting for empty queue:" + measurementsQueue.size() + " and worker thread stop:" + worker.isRunning());
            }  //This wont run indefinitely. If the worker thread cant send the measurements it will call abort

            hasStopped = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideStoppingDialog();
            //show dialog
            returnToMainMenu();
        }
    }

    private void closeDevice() {
        if(device != null) {
            device.close();
        } else if(simulatedDevice != null) {
            simulatedDevice.close();
        }
    }


    private void returnToMainMenu() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getQuestionnaire().setCurrentNode(MainQuestionnaire.getInstance().getMainMenu());
            }
        });
    }

    private void hideStoppingDialog() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress != null && progress.isShowing()) {
                    progress.hide();
                }
            }
        });
    }

    private void showStoppingDialog() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress = new ProgressDialog(questionnaire.getActivity());
                progress.setTitle(questionnaire.getActivity().getString(R.string.realtime_ctg_stopping));
                progress.setMessage(questionnaire.getActivity().getString(R.string.realtime_ctg_stopping_detail));
                progress.setCancelable(false);

                progress.show();
            }
        });
    }

    private void showStartingDialog() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress = new ProgressDialog(questionnaire.getActivity());
                progress.setTitle(questionnaire.getActivity().getString(R.string.realtime_ctg_starting));
                progress.setMessage(questionnaire.getActivity().getString(R.string.realtime_ctg_starting_detail));
                progress.setCancelable(false);

                progress.show();
            }
        });
    }

    private void hideStartingDialog() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress != null && progress.isShowing()) {
                    progress.hide();
                }
            }
        });
    }


    private class setupWorkerAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showStartingDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            worker = new RealtimeCTGMeasurementsExportWorker(CommunicatorFactory.getCommunicator(questionnaire), measurementsQueue, patientInfo, registrationIdentifier, RealTimeCTGNode.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            hideStartingDialog();
            worker.start();
        }
    }


    @Override
    public void addSamples(float[] mhr, float[] fhr, int[] qfhr, float[] toco, Date readTime) {
        Log.d(TAG, "Got sample");
        sampleCount++;
        boolean didAddMessage = measurementsQueue.offer(new SampleMessage(mhr, fhr, qfhr, toco, sampleCount, readTime));

        if(!didAddMessage) {
            abort(Util.getString(R.string.realtime_ctg_stopped_network_error, questionnaire.getContext()));
            OpenTeleApplication.instance().logMessage("Aborted Realtime-ctg due to overflow of measurementsQueue");
        }
    }

    @Override
    public void updateProgress(int i, int samples) {
        this.questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(Util.getString(R.string.monica_receiving_data, questionnaire));
            }
        });
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
    public void abort(final String reason) {
        if(hasStopped) {
            return;
        }

        if(isStopping) { //Error occurred while trying to stop the worker.
            if(waitForEmptyQueue != null && !waitForEmptyQueue.isCancelled()) {
                waitForEmptyQueue.cancel(true);
            }

            hideStoppingDialog();
            hasStopped = true;

            returnToMainMenu();
            closeDevice();
            return;

        }

        this.questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog(reason);
                statusTextView.setText(Util.getString(R.string.realtime_ctg_stopped_network_error, questionnaire));
            }
        });
        closeDevice();
    }

    private void showDialog(String reason) {
        AlertDialog alertDialog = new AlertDialog.Builder(this.questionnaire.getActivity()).create();
        alertDialog.setMessage(reason);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, Util.getString(R.string.default_ok, questionnaire.getActivity()), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getQuestionnaire().setCurrentNode(MainQuestionnaire.getInstance().getMainMenu());
            }
        });
        alertDialog.show();
    }

    @Override
    public void done(String message) {
        //Called in error MonicaDeviceController in error siutations
        abort(message);
    }

    @Override
    public void done() {}

    @Override
    public void setStartVoltage(float voltage) {}

    @Override
    public void setEndVoltage(float voltage) {}

    @Override
    public void setStartTimeValue(Date dateTime) {}

    @Override
    public void setEndTimeValue(Date dateTime) {}

    @Override
    public void addSignal(Date dateTime) {
        measurementsQueue.add(new SignalMessage(dateTime));
    }

    @Override
    public void setDeviceIdString(String deviceId) {}

    @Override
    public int getSampleTimeMinutes() {
        return 43200; //One month should be sufficient
    }

    @Override
    public Date getStartTimeValue() {
        return null;
    }

    @Override
    public void addFetalHeight(int fetalHeight) {}

    @Override
    public void addSignalToNoise(int signalToNoise) {}

    public String getDeviceName() {
        return this.deviceName;
    }

    public void connectionProblems() {
        abort(Util.getString(R.string.realtime_ctg_stopped_network_error, questionnaire));
    }
}
