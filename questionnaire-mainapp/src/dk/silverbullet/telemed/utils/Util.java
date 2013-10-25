package dk.silverbullet.telemed.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.Element;
import dk.silverbullet.telemed.questionnaire.element.ElementAdapter;
import dk.silverbullet.telemed.questionnaire.expression.Expression;
import dk.silverbullet.telemed.questionnaire.expression.ExpressionInterfaceAdapter;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableAdapter;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.expression.VariableNotDeclaredException;
import dk.silverbullet.telemed.questionnaire.expression.VariableTypeMissing;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.NodeAdapter;

@SuppressLint("SimpleDateFormat")
public final class Util {

    private static final String DEFAULT_SERVER_URL = "https://opentele-devel.silverbullet.dk/opentele-server/";
    // private static final String DEFAULT_SERVER_URL = "https://teleskejby-devel.silverbullet.dk/teleskejby-server/";
    // private static final String DEFAULT_SERVER_URL = "http://datamon-test.rn.dk:443/opentele-server/";

    @SuppressWarnings("unused")
    private static final String TAG = getTag(Util.class);

    public static final String PREFS_NAME = "MyPrefsFile";

    public static final String VARIABLE_IS_LOGGED_IN = "VARIABLE_IS_LOGGED_IN";
    public static final String VARIABLE_CLIENT_SUPPORTED = "CLIENT_VERSION_SUPPORTED";
    public static final String VARIABLE_USERNAME = "USERNAME";
    public static final String VARIABLE_PASSWORD = "PASSWORD";
    public static final String VARIABLE_ID = "ID";
    public static final String VARIABLE_SERVER_IP = "VARIABLE_SERVER_IP";
    public static final String VARIABLE_SHOW_UPLOAD_DEBUG = "VARIABLE_SHOW_UPLOAD_DEBUG";
    public static final String VARIABLE_REAL_NAME = "VARIABLE_REAL_NAME";
    public static final String VARIABLE_USER_ID = "VARIABLE_USER_ID";
    public static final String VARIABLE_CHANGE_PASSWORD = "VARIABLE_CHANGE_PASSWORD";
    public static final String VARIABLE_CURRENT_PASSWORD = "VARIABLE_CURRENT_PASSWORD";
    public static final String VARIABLE_ALARM_TEST = "VARIABLE_ALARM_TEST";
    public static final String VARIABLE_MESSAGE_TEXT = "VARIABLE_MESSAGE_TEXT";

    public static final String PREF_SERVER_IP = "PREF_SERVER_IP";
    public static final String PREF_SHOW_UPLOAD_DEBUG = "PREF_SHOW_UPLOAD_DEBUG";
    public static final String SERVER_ENVIRONMENT = "SERVER_ENVIRONMENT";
    public static final String ADMINUSER_NAME = "admin";
    public static final String ADMINUSER_PASS = "admin";

    public static final SimpleDateFormat ISO8601_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final SimpleDateFormat ISO8601_DATE_TIME_FORMAT_SHORT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

    static { // 'Zulu time' in accordance with ISO-8601: http://en.wikipedia.org/wiki/Iso8601#UTC
        ISO8601_DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        ISO8601_DATE_TIME_FORMAT_SHORT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String getServerUrl(Questionnaire questionnaire) {
        String serverUrl = DEFAULT_SERVER_URL;

        if (hasStaticServerUrl(questionnaire)) {
            serverUrl = staticServerUrl(questionnaire);
        }
        if (!isServerUrlLocked(questionnaire) && hasServerUrlInSettings(questionnaire)) {
            serverUrl = serverUrlFromSettings(questionnaire);
        }

        if (!serverUrl.endsWith("/")) {
            serverUrl += "/";
        }
        return serverUrl;
    }

    public static URI getServerUrl(Questionnaire questionnaire, String x) throws URISyntaxException, IOException {
        String url = getServerUrl(questionnaire);
        url += x;

        // Test the server...
        new URL(url).openConnection().connect();

        return new URI(url);
    }

    public static boolean isServerUrlLocked(Questionnaire questionnaire) {
        return "true".equals(questionnaire.getActivity().getString(R.string.server_url_locked));
    }

    private static String staticServerUrl(Questionnaire questionnaire) {
        return questionnaire.getActivity().getString(R.string.server_url);
    }

    private static boolean hasStaticServerUrl(Questionnaire questionnaire) {
        String staticServerUrl = staticServerUrl(questionnaire);
        return staticServerUrl != null && !"".equals(staticServerUrl) && !"${server.url}".equals(staticServerUrl);
    }

    private static boolean hasServerUrlInSettings(Questionnaire questionnaire) {
        return questionnaire.getValuePool().get(Util.VARIABLE_SERVER_IP) != null;
    }

    private static String serverUrlFromSettings(Questionnaire questionnaire) {
        return (String) questionnaire.getValuePool().get(Util.VARIABLE_SERVER_IP).getExpressionValue().getValue();
    }

    public static void showToast(Questionnaire questionnaire, String text) {

        LayoutInflater inflater = questionnaire.getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) questionnaire.getActivity().findViewById(R.id.custom_toast_layout_id));

        TextView text2 = (TextView) layout.findViewById(R.id.text);
        text2.setText(text);

        Toast toast = new Toast(questionnaire.getActivity().getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void showDialog(Questionnaire questionnaire, String text) {
        final Dialog dialog = new Dialog(questionnaire.getActivity());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Title...");

        TextView textView = (TextView) dialog.findViewById(R.id.text);
        textView.setText(text);
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_launcher);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Element.class, new ElementAdapter());
        builder.registerTypeAdapter(Node.class, new NodeAdapter());
        Object expressionInterfaceAdapter = new ExpressionInterfaceAdapter<Expression<?>>();
        builder.registerTypeAdapter(Expression.class, expressionInterfaceAdapter);
        builder.registerTypeAdapter(Variable.class, new VariableAdapter());
        return builder.excludeFieldsWithoutExposeAnnotation().create();
    }

    public static Gson getGsonForOutput() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Variable.class, new VariableAdapter());
        return builder.excludeFieldsWithoutExposeAnnotation().create();
    }

    public static String getTag(Class<?> klass) {
        return klass.getName().substring(klass.getName().lastIndexOf(".") + 1);
    }

    public static short calcCRC16(byte[] bytes) {
        short crc = 0; // initial value
        short polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }

        return crc;
    }

    public static int getUnsignedIntBits(byte[] in, int from, int len) {
        int value = 0;
        for (int x = from / 8; x < (7 + from + len) / 8; x++)
            value = (value << 8) | (in[x] & 0xFF);

        final int mask = ~(~0 << len);
        final int shift = (8 - (from + len) % 8) % 8;

        return (value >> shift) & mask;
    }

    public static String toString(float[] f) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < f.length; i++) {
            if (i != 0)
                sb.append(", ");
            sb.append(f[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    public static String toString(int[] f) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < f.length; i++) {
            if (i != 0)
                sb.append(", ");
            sb.append(f[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    public static String toString(Object[] f) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < f.length; i++) {
            if (i != 0)
                sb.append(", ");
            sb.append(f[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T> Variable<T> linkVariable(Map<String, Variable<?>> variablePool, Variable<T> var)
            throws VariableLinkFailedException {
        return linkVariable(variablePool, var, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> Variable<T> linkVariable(Map<String, Variable<?>> variablePool, Variable<T> var, boolean optional)
            throws VariableLinkFailedException {

        if (var == null) {
            if (optional) {
                return null;
            } else {
                throw new VariableNotDeclaredException();
            }
        }

        if (variablePool.containsKey(var.getName())) {
            var = (Variable<T>) variablePool.get(var.getName());
            if (var.getType() == null) {
                throw new VariableTypeMissing(var.getName());
            }
            return var;
        } else {
            throw new UnknownVariableException(var.getName());
        }
    }

    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static final float random(final float pMin, final float pMax) {
        Random random = new Random(System.nanoTime());
        return pMin + random.nextFloat() * (pMax - pMin);
    }

    @SuppressWarnings("unchecked")
    public static void saveVariables(Questionnaire questionnaire) {
        Variable<?> serverIP = questionnaire.getValuePool().get(Util.VARIABLE_SERVER_IP);
        Variable<Boolean> showUploadDebugNode = (Variable<Boolean>) questionnaire.getValuePool().get(
                Util.VARIABLE_SHOW_UPLOAD_DEBUG);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(questionnaire.getActivity())
                .edit();

        editor.putString(PREF_SERVER_IP, serverIP.getExpressionValue().toString());
        editor.putBoolean(PREF_SHOW_UPLOAD_DEBUG, showUploadDebugNode.getExpressionValue().getValue());

        editor.commit();
    }

    public static final String getStringVariableValue(Questionnaire questionnaire, String name) {
        String result = null;
        Variable<?> variable = questionnaire.getValuePool().get(name);
        if (null != variable && null != variable.getExpressionValue())
            result = variable.getExpressionValue().toString();

        return result;
    }

    public static String toHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        String hex = "0123456789abcdef";
        for (byte b : bytes) {
            sb.append(hex.charAt((b >> 4) & 0xf)).append(hex.charAt(b & 0xf));
        }
        return sb.toString();
    }

    public static byte[] hex2bytes(String hex) {
        String raw = hex.replaceAll("[\\.,\\ -]", "");
        if (raw.length() % 2 != 0)
            throw new IllegalArgumentException("Illegal hex string length:" + raw.length() + " \"" + hex + "\"");
        byte[] bytes = new byte[raw.length() / 2];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < bytes.length; i++) {
            int highNible = hexDigits.indexOf(raw.charAt(i * 2));
            if (highNible < 0 || highNible > 15)
                throw new IllegalArgumentException("Illegal hex digit: " + raw.charAt(i * 2));
            int lowNible = hexDigits.indexOf(raw.charAt(i * 2 + 1));
            if (lowNible < 0 || lowNible > 15)
                throw new IllegalArgumentException("Illegal hex digit: " + raw.charAt(i * 2 + 1));
            bytes[i] = (byte) ((highNible << 4) | lowNible);
        }
        return bytes;
    }

    public static String escapeHtml(String string) {
        return string.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");
    }

    public static String formatTime(Date date) {
        long ageInHours = (System.currentTimeMillis() - date.getTime()) / (1000 * 60 * 60);
        long ageInDays = ageInHours / 24;

        if (ageInHours < 0)
            return new SimpleDateFormat("d/M yyyy HH:mm").format(date);

        if (ageInDays > 350)
            return new SimpleDateFormat("d/M yyyy").format(date);
        if (ageInDays > 30)
            return new SimpleDateFormat("d/M").format(date);

        if (ageInHours < 20)
            return new SimpleDateFormat("HH:mm").format(date);

        return new SimpleDateFormat("d/M HH:mm").format(date);
    }

}
