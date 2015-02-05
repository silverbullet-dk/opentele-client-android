package dk.silverbullet.telemed.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.*;
import dk.silverbullet.telemed.rest.client.ServerInformation;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.auth.BasicScheme;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressLint("SimpleDateFormat")
public final class Util {
    private static final String DEFAULT_SERVER_URL = "https://opentele-devel.silverbullet.dk/opentele-server/";

    @SuppressWarnings("unused")
    private static final String TAG = getTag(Util.class);

    public static final String VARIABLE_IS_LOGGED_IN = "VARIABLE_IS_LOGGED_IN";
    public static final String VARIABLE_IS_LOGGED_IN_AS_ADMIN = "VARIABLE_IS_LOGGED_IN_AS_ADMIN";
    public static final String VARIABLE_CLIENT_SUPPORTED = "CLIENT_VERSION_SUPPORTED";
    public static final String VARIABLE_USERNAME = "USERNAME";
    public static final String VARIABLE_PASSWORD = "PASSWORD";
    public static final String VARIABLE_ID = "ID";
    public static final String VARIABLE_SERVER_IP = "VARIABLE_SERVER_IP";
    public static final String VARIABLE_SHOW_UPLOAD_DEBUG = "VARIABLE_SHOW_UPLOAD_DEBUG";
    public static final String VARIABLE_REAL_NAME = "VARIABLE_REAL_NAME";
    public static final String VARIABLE_USER_ID = "VARIABLE_USER_ID";
    public static final String VARIABLE_ALARM_TEST = "VARIABLE_ALARM_TEST";
    public static final String VARIABLE_MESSAGE_TEXT = "VARIABLE_MESSAGE_TEXT";
    public static final String VARIABLE_SHOW_REALTIME_CTG_MENU = "VARIABLE_SHOW_REALTIME_CTG_MENU";

    public static final String PREF_SERVER_IP = "PREF_SERVER_IP";
    public static final String PREF_SHOW_UPLOAD_DEBUG = "PREF_SHOW_UPLOAD_DEBUG";
    public static final String SERVER_ENVIRONMENT = "SERVER_ENVIRONMENT";
    public static final String ADMINUSER_NAME = "admin";
    public static final String ADMINUSER_PASS = "admin";

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

    public static boolean isServerUrlLocked(Questionnaire questionnaire) {
        return "true".equals(questionnaire.getContext().getString(R.string.server_url_locked));
    }

    public static boolean shouldClearUserNameOnLogin(Questionnaire questionnaire) {
        return "true".equals(questionnaire.getContext().getString(R.string.clear_user_name_on_login));
    }

    public static boolean shouldHidePasswordText(Questionnaire questionnaire) {
        // Using the same setting as "clear user name on login"
        return shouldClearUserNameOnLogin(questionnaire);
    }

    private static String staticServerUrl(Questionnaire questionnaire) {
        return questionnaire.getContext().getString(R.string.server_url);
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

        Toast toast = new Toast(questionnaire.getContext().getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
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
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < f.length; i++) {
            if (i != 0)
                builder.append(", ");
            builder.append(f[i]);
        }
        builder.append("]");
        return builder.toString();
    }

    public static String toString(int[] f) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < f.length; i++) {
            if (i != 0)
                builder.append(", ");
            builder.append(f[i]);
        }
        builder.append("]");
        return builder.toString();
    }

    public static String toString(Object[] f) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < f.length; i++) {
            if (i != 0)
                builder.append(", ");
            builder.append(f[i]);
        }
        builder.append("]");
        return builder.toString();
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

    public static float random(final float pMin, final float pMax) {
        Random random = new Random(System.nanoTime());
        return pMin + random.nextFloat() * (pMax - pMin);
    }

    @SuppressWarnings("unchecked")
    public static void saveVariables(Questionnaire questionnaire) {
        Variable<?> serverIP = questionnaire.getValuePool().get(Util.VARIABLE_SERVER_IP);
        Variable<Boolean> showUploadDebugNode = (Variable<Boolean>) questionnaire.getValuePool().get(
                Util.VARIABLE_SHOW_UPLOAD_DEBUG);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(questionnaire.getContext())
                .edit();

        editor.putString(PREF_SERVER_IP, serverIP.getExpressionValue().toString());
        editor.putBoolean(PREF_SHOW_UPLOAD_DEBUG, showUploadDebugNode.getExpressionValue().getValue());

        editor.commit();
    }

    public static String getStringVariableValue(Questionnaire questionnaire, String name) {
        String result = null;
        Variable<?> variable = questionnaire.getValuePool().get(name);
        if (null != variable && null != variable.getExpressionValue()) {
            result = variable.getExpressionValue().toString();
        }

        return result;
    }

    public static void setStringVariableValue(Questionnaire questionnaire, String name, String value) {
        Variable<String> variable = (Variable<String>) questionnaire.getValuePool().get(name);
        variable.setValue(value);
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        String hex = "0123456789abcdef";
        for (byte b : bytes) {
            builder.append(hex.charAt((b >> 4) & 0xf)).append(hex.charAt(b & 0xf));
        }
        return builder.toString();
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

    public static void setHeaders(HttpRequestBase request, ServerInformation serverInformation, Boolean useImageHeader) {
        String clientVersion = serverInformation.getContext().getString(R.string.client_version);
        String userName = serverInformation.getUserName();
        String password = serverInformation.getPassword();

        setHeaders(request, clientVersion, userName, password, useImageHeader);
    }

    public static void setHeaders(HttpRequestBase request, ServerInformation serverInformation) {
        setHeaders(request, serverInformation, false);
    }

    public static void setHeaders(HttpRequestBase request, String clientVersion, String userName, String password, Boolean useImageHeader) {
        if (!useImageHeader) {
            request.setHeader("Content-type", "application/json");
            request.setHeader("Accept", "application/json");
            request.setHeader("X-Requested-With", "json");
        } else {
            request.setHeader("Content-type", "application/octet-stream");
            request.setHeader("Accept", "application/octet-stream");
//            request.setHeader("X-Requested-With", "octet-stream");
        }
        request.setHeader("Client-version", clientVersion);
        request.setHeader("User-Agent", userAgentString(clientVersion));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        request.setHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
    }

    public static void setHeaders(HttpRequestBase request, String clientVersion, String userName, String password) {
        setHeaders(request, clientVersion, userName, password, false);
    }

    public static String join(List<String> strings, String separator) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (String s : strings) {
            if (!isFirst) {
                builder.append(separator);
            }
            isFirst = false;
            builder.append(s);
        }
        return builder.toString();
    }

    public static String getString(int resourceId, Questionnaire questionnaire) {
        return Util.getString(resourceId, questionnaire.getContext());
    }

    public static String getString(int resourceId, Context context) {
        return context.getString(resourceId);
    }

    public static String getString(int resourceId, Context context, Object... formatArgs) {
        return context.getString(resourceId, formatArgs);
    }

    public static String getString(int resourceId, Questionnaire questionnaire, Object... formatArgs) {
        return Util.getString(resourceId, questionnaire.getContext(), formatArgs);
    }

    private static String userAgentString(String clientVersion) {
        // We'll construct a User-Agent string which resembles browser strings a bit.
        // Have a look here:
        // http://user-agents.my-addr.com/user_agent_request/user_agent_examples-and-user_agent_types.php
        return "Android/" + Build.VERSION.RELEASE +
                " [" + Locale.getDefault().getLanguage() + "]" +
                " (" + Build.MANUFACTURER + ";" +
                " " + Build.MODEL + ";" +
                " " + Build.PRODUCT + ";" +
                " " + Build.BRAND + ";" +
                " " + Build.DEVICE + ")" +
                " OpenTeleClient/" + clientVersion;
    }
}
