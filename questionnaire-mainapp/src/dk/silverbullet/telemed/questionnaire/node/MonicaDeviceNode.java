package dk.silverbullet.telemed.questionnaire.node;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.device.AmbiguousDeviceException;
import dk.silverbullet.telemed.device.BluetoothDisabledException;
import dk.silverbullet.telemed.device.BluetoothNotAvailableException;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.monica.MonicaDevice;
import dk.silverbullet.telemed.device.monica.MonicaDeviceController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.Expression;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.monica.DeviceState;
import dk.silverbullet.telemed.questionnaire.node.monica.MonicaDeviceCallback;
import dk.silverbullet.telemed.questionnaire.node.monica.SimulatedMonicaDevice;
import dk.silverbullet.telemed.utils.ProgressiveProgress;
import dk.silverbullet.telemed.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static dk.silverbullet.telemed.utils.Json.ISO8601_DATE_TIME_FORMAT;
import static dk.silverbullet.telemed.utils.Util.linkVariable;

public class MonicaDeviceNode extends DeviceNode implements MonicaDeviceCallback, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = Util.getTag(MonicaDeviceNode.class);

    ProgressiveProgress progressiveProgress;

    @Expose
    private Expression<Boolean> runAsSimulator; // Set to true to run as simulation only
    @Expose
    private Expression<Integer> measuringTime; // Number of minutes to get samples from the device
    @Expose
    private Variable<Float[]> fhr; // (Fetal Heart Rate, fostrets hjerterytme)
    @Expose
    private Variable<Float[]> mhr; // (Maternal Heart Rate - moderens hjerterytme)
    @Expose
    private Variable<Integer[]> qfhr; // (Quality measurements for FHR - noget med hvor godt signalet er 0..3)
    @Expose
    private Variable<Integer[]> fetalHeight;

    @Expose
    private Variable<Integer[]> signalToNoise;

    @Expose
    private Variable<Float[]> toco; // Livmoderbevægelser
    @Expose
    private Variable<String[]> signal; // tidspunkter for tryk på den lyserøde knap, ISO-8601 -format
    @Expose
    private Variable<Float> voltageStart; // batterispænding ved start
    @Expose
    private Variable<Float> voltageEnd; // batterispænding ved afslutning

    private transient List<float[]> mhrBuf;
    private transient List<float[]> fhrBuf;
    private transient List<float[]> tocoBuf;
    private transient List<int[]> qfhrBuf;
    private transient List<Integer> fetalHeightBuf;
    private transient List<Integer> signalToNoiseBuf;
    private transient List<String> signalBuf;

    private transient TextView progressStatusText;

    private transient ProgressBar mainProgress;
    private transient SeekBar measureTime;
    private transient TextView measureTimeText;
    private transient LinearLayout userTimeSet;

    private transient CheckBox buttonOrange;
    private transient CheckBox buttonWhite;
    private transient CheckBox buttonGreen;
    private transient CheckBox buttonBlack;
    private transient CheckBox buttonYellow;

    private int sampleTimeInMinutes = 30;

    private long start;

    private DeviceState currentState;

    private MonicaDevice device;

    public MonicaDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        Log.d(TAG, "Enter...");
        super.enter();
        Activity activity = questionnaire.getActivity();
        inflateView(activity);

        initialize(activity);
        try {
            if (runAsSimulator != null && runAsSimulator.evaluate() != null && runAsSimulator.evaluate())
                device = new SimulatedMonicaDevice(this);
            else
                device = new MonicaDeviceController(this, activity);
        } catch (BluetoothDisabledException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(questionnaire.getActivity().getApplicationContext(), Util.getString(R.string.monica_bluetooth_off, questionnaire),
                    Toast.LENGTH_LONG).show();
            questionnaire.setCurrentNode(getNextFailNode());
        } catch (BluetoothNotAvailableException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(questionnaire.getActivity().getApplicationContext(),
                    Util.getString(R.string.monica_bluetooth_unavaliable, questionnaire), Toast.LENGTH_LONG).show();
            questionnaire.setCurrentNode(getNextFailNode());
        } catch (AmbiguousDeviceException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(questionnaire.getActivity().getApplicationContext(),
                    Util.getString(R.string.monica_multiple_devices, questionnaire), Toast.LENGTH_LONG).show();
            questionnaire.setCurrentNode(getNextFailNode());
        } catch (DeviceInitialisationException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(questionnaire.getActivity().getApplicationContext(), Util.getString(R.string.monica_failed_to_start, questionnaire),
                    Toast.LENGTH_LONG).show();
            questionnaire.setCurrentNode(getNextFailNode());
        }
    }

    private void inflateView(Activity activity) {
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View monicaView = inflater.inflate(R.layout.monica, null);

        rootLayout.removeAllViews();
        rootLayout.addView(monicaView);
    }

    private void initialize(Activity activity) {
        mainProgress = (ProgressBar) activity.findViewById(R.id.mainProgress);
        userTimeSet = (LinearLayout) activity.findViewById(R.id.userTimeSet);
        progressiveProgress = new ProgressiveProgress(15, 10, 5, 15, 30);
        Log.d(TAG, "measuringTime: " + measuringTime);
        if (measuringTime == null || measuringTime.evaluate() == null || measuringTime.evaluate() == 0) {
            measureTime = (SeekBar) activity.findViewById(R.id.measureTime);
            measureTimeText = (TextView) activity.findViewById(R.id.measureTimeText);
            measureTime.setOnSeekBarChangeListener(this);
            measureTime.setMax(progressiveProgress.getStepCount() - 1);
            sampleTimeInMinutes = 30;
            measureTime.setProgress(progressiveProgress.value2step(sampleTimeInMinutes));
        } else {
            sampleTimeInMinutes = measuringTime.evaluate();
            userTimeSet.setVisibility(View.GONE);
        }

        progressStatusText = (TextView) activity.findViewById(R.id.progressStatusText);

        buttonOrange = (CheckBox) activity.findViewById(R.id.buttonOrange);
        buttonWhite = (CheckBox) activity.findViewById(R.id.buttonWhite);
        buttonGreen = (CheckBox) activity.findViewById(R.id.buttonGreen);
        buttonBlack = (CheckBox) activity.findViewById(R.id.buttonBlack);
        buttonYellow = (CheckBox) activity.findViewById(R.id.buttonYellow);

        buttonOrange.setChecked(false);
        buttonWhite.setChecked(false);
        buttonGreen.setChecked(false);
        buttonBlack.setChecked(false);
        buttonYellow.setChecked(false);

        final int SAMPLE_SECONDS = 3600;
        mhrBuf = new ArrayList<float[]>(SAMPLE_SECONDS);
        fhrBuf = new ArrayList<float[]>(SAMPLE_SECONDS);
        tocoBuf = new ArrayList<float[]>(SAMPLE_SECONDS);
        qfhrBuf = new ArrayList<int[]>(SAMPLE_SECONDS);
        fetalHeightBuf = new ArrayList<Integer>(SAMPLE_SECONDS / 20);
        signalToNoiseBuf = new ArrayList<Integer>(SAMPLE_SECONDS / 20);
        signalBuf = new ArrayList<String>();

    }

    private String[] asStringArray(List<String> stringList) {
        String[] stringArray = new String[stringList.size()];
        int i = 0;
        for (String string : stringList) {
            stringArray[i++] = string;
        }
        return stringArray;
    }

    private Float[] asFloatArray(List<float[]> mhr2) {
        int size = 0;
        for (float[] fa : mhr2) {
            size += fa.length;
        }
        Float[] result = new Float[size];
        int i = 0;
        for (float[] fa : mhr2) {
            for (float f : fa) {
                result[i++] = f;
            }
        }

        return result;
    }

    private Integer[] asIntegerArray(List<int[]> mhr2) {
        int size = 0;
        for (int[] fa : mhr2) {
            size += fa.length;
        }
        Integer[] result = new Integer[size];
        int i = 0;
        for (int[] ia : mhr2) {
            for (int ii : ia) {
                result[i++] = ii;
            }
        }

        return result;
    }

    @Override
    public void addSamples(float[] mhr, float[] fhr, int[] qfhr, float[] toco) {
        this.mhrBuf.add(mhr);
        this.fhrBuf.add(fhr);
        this.qfhrBuf.add(qfhr);
        this.tocoBuf.add(toco);
    }

    @Override
    public void updateProgress(final int current, final int total) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainProgress.setMax(total);
                mainProgress.setProgress(current);
                setState(DeviceState.RECEIVING_DATA);
                // int pct = (current * 100) / total;
                // progressText.setText(pct + "%");
            }
        });
    }

    @Override
    public void setProbeState(final boolean orange, final boolean white, final boolean green, final boolean black,
            final boolean yellow) {

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
        if (currentState == state)
            return;

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
        case PROCESSING_DATA:
            text = Util.getString(R.string.monica_processing_data, questionnaire);
            break;
        case CLOSING:
            text = Util.getString(R.string.monica_closing, questionnaire);
            break;
        default:
            text = Util.getString(R.string.monica_question_marks, questionnaire);
            break;
        }

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressStatusText.setText(text);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        fhr = linkVariable(variablePool, fhr);
        mhr = linkVariable(variablePool, mhr);
        qfhr = linkVariable(variablePool, qfhr);
        fetalHeight = linkVariable(variablePool, fetalHeight);
        signalToNoise = linkVariable(variablePool, signalToNoise);
        toco = linkVariable(variablePool, toco);
        signal = linkVariable(variablePool, signal);
        voltageStart = linkVariable(variablePool, voltageStart);
        voltageEnd = linkVariable(variablePool, voltageEnd);
        if (runAsSimulator != null) {
            if (runAsSimulator instanceof Variable) {
                String name = ((Variable<Boolean>) runAsSimulator).getName();
                if (variablePool.containsKey(name))
                    runAsSimulator = (Expression<Boolean>) variablePool.get(name);
                else
                    throw new UnknownVariableException(name);
            } else
                runAsSimulator.link(variablePool);
        }
        if (measuringTime != null) {
            if (measuringTime instanceof Variable) {
                String name = ((Variable<Integer>) measuringTime).getName();
                if (variablePool.containsKey(name)) {
                    measuringTime = (Expression<Integer>) variablePool.get(name);
                } else
                    throw new UnknownVariableException(name);
            } else
                measuringTime.link(variablePool);
        }
    }

    @Override
    public void abort(final String message) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressStatusText.setText(message);
                Toast.makeText(questionnaire.getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                questionnaire.setCurrentNode(getNextFailNode());
            }
        });
    }

    @Override
    public void done(final String message) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressStatusText.setText(message);
                Toast.makeText(questionnaire.getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                questionnaire.setCurrentNode(getNextNode());
            }
        });
    }

    @Override
    public void done() {
        Log.d(TAG, "Done!");
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                questionnaire.setCurrentNode(getNextNode());
            }
        });
    }

    @Override
    public void setStartTimeValue(Date startTime) {
        super.setStartTimeValue(startTime);
        start = startTime.getTime();
    };

    @Override
    public Date getStartTimeValue() {
        if (start == 0)
            return null;
        return new Date(start);
    }

    @Override
    public void setStartVoltage(float voltage) {
        Log.d(TAG, "start voltage: " + voltage);
        if (voltageStart != null)
            voltageStart.setValue(voltage);
    }

    @Override
    public void setEndVoltage(float voltage) {
        Log.d(TAG, "end voltage: " + voltage);
        if (voltageEnd != null)
            voltageEnd.setValue(voltage);
    }

    public Expression<Boolean> getRunAsSimulator() {
        return runAsSimulator;
    }

    public void setRunAsSimulator(Expression<Boolean> runAsSimulator) {
        this.runAsSimulator = runAsSimulator;
    }

    public Variable<Float[]> getFhr() {
        return fhr;
    }

    public void setFhr(Variable<Float[]> fhr) {
        this.fhr = fhr;
    }

    public Variable<Float[]> getMhr() {
        return mhr;
    }

    public void setMhr(Variable<Float[]> mhr) {
        this.mhr = mhr;
    }

    public Variable<Integer[]> getQfhr() {
        return qfhr;
    }

    public void setFetalHeight(Variable<Integer[]> fetalHeight) {
        this.fetalHeight = fetalHeight;
    }

    public void setSignalToNoise(Variable<Integer[]> signalToNoise) {
        this.signalToNoise = signalToNoise;
    }

    public void setQfhr(Variable<Integer[]> qfhr) {
        this.qfhr = qfhr;
    }

    public Variable<Float[]> getToco() {
        return toco;
    }

    public void setToco(Variable<Float[]> toco) {
        this.toco = toco;
    }

    public Variable<String[]> getSignal() {
        return signal;
    }

    public void setSignal(Variable<String[]> signal) {
        this.signal = signal;
    }

    public Variable<Float> getVoltageStart() {
        return voltageStart;
    }

    public void setVoltageStart(Variable<Float> voltageStart) {
        this.voltageStart = voltageStart;
    }

    public Variable<Float> getVoltageEnd() {
        return voltageEnd;
    }

    public void setVoltageEnd(Variable<Float> voltageEnd) {
        this.voltageEnd = voltageEnd;
    }

    @Override
    public void addSignal(Date dateTime) {
        Log.d(TAG, "dt=" + dateTime);
        Log.d(TAG, "dtF=" + ISO8601_DATE_TIME_FORMAT.format(dateTime));
        Log.d(TAG, "sb=" + signalBuf);
        Log.d(TAG, "sbZ=" + signalBuf.size());

        signalBuf.add(ISO8601_DATE_TIME_FORMAT.format(dateTime));
    }

    @Override
    public void deviceLeave() {
        if (device != null)
            device.close();
        mhr.setValue(asFloatArray(mhrBuf));
        mhrBuf.clear();
        fhr.setValue(asFloatArray(fhrBuf));
        fhrBuf.clear();
        qfhr.setValue(asIntegerArray(qfhrBuf));
        qfhrBuf.clear();
        fetalHeight.setValue(fetalHeightBuf.toArray(new Integer[fetalHeightBuf.size()]));
        fetalHeightBuf.clear();
        signalToNoise.setValue(signalToNoiseBuf.toArray(new Integer[signalToNoiseBuf.size()]));
        signalToNoiseBuf.clear();
        // fmp.setExpressionValue(asIntegerArray(fmpBuf)); // ??
        toco.setValue(asFloatArray(tocoBuf));
        tocoBuf.clear();
        signal.setValue(asStringArray(signalBuf));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser)
            return; // Ignore setting the progress time internally!

        int progressTime = updateMeasuringTime(progress);

        measureTimeText.setText(Util.getString(R.string.monica_set_time, questionnaire) + getSampleTimeText(progressTime));
    }

    private int updateMeasuringTime(int progress) {
        int min = calculateMinTime();

        int progressTime = progressiveProgress.step2value(progress);

        if (progressTime < min) {
            progressTime = min;
            measureTime.setProgress(progressiveProgress.value2step(progressTime));
        } else if (progressTime > progressiveProgress.getHighestValue()) {
            progressTime = progressiveProgress.getHighestValue();
            measureTime.setProgress(progressiveProgress.getStepCount());
        }
        return progressTime;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        int progressTime = updateMeasuringTime(seekBar.getProgress());

        measureTimeText.setText(Util.getString(R.string.monica_set_time, questionnaire) + getSampleTimeText(progressTime));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sampleTimeInMinutes = updateMeasuringTime(seekBar.getProgress());

        measureTimeText.setText(Util.getString(R.string.monica_time_set_to, questionnaire) + getSampleTimeText(sampleTimeInMinutes));
    }

    private String getSampleTimeText(int minutes) {
        String sampleTimeText = minutes / 60 + ":";
        if (minutes % 60 < 10)
            sampleTimeText += "0";
        sampleTimeText += minutes % 60;
        return sampleTimeText;
    }

    private int calculateMinTime() {
        if (start == 0)
            return 15;
        int duration = (int) ((System.currentTimeMillis() - start + 60000) / 60000);
        return Math.max(15, duration);
    }

    @Override
    public int getSampleTimeMinutes() {
        return sampleTimeInMinutes;
    }

    public void setMeasuringTime(Expression<Integer> measuringTime) {
        this.measuringTime = measuringTime;
    }

    public Expression<Integer> getMeasuringTime() {
        return measuringTime;
    }

    @Override
    public void addFetalHeight(int fetalHeight) {
        fetalHeightBuf.add(fetalHeight);

    }

    @Override
    public void addSignalToNoise(int signalToNoise) {
        signalToNoiseBuf.add(signalToNoise);
    }

}
