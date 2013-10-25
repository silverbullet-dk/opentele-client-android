package dk.silverbullet.telemed.questionnaire.node;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

public class WebViewNode extends IONode {

    private final String url;
    private final String title;

    public WebViewNode(Questionnaire questionnaire, String nodeName, String url, String title) {
        super(questionnaire, nodeName);
        this.url = url;
        this.title = title;
    }

    @Override
    public void enter() {
        clearElements();
        inflateLayout();

        showDialog();
        ViewGroup rootLayout = questionnaire.getRootLayout();

        linkTopPanel(rootLayout);

        super.enter();
    }

    private ProgressDialog dialog;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    private void inflateLayout() {
        Activity activity = questionnaire.getActivity();
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View webviewLayout = inflater.inflate(R.layout.webview_node, null);
        webView = (WebView) webviewLayout.findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);

        CookieSyncManager.createInstance(activity);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(false);
        cookieManager.removeAllCookie();

        TextView headlineTextView = (TextView) webviewLayout.findViewById(R.id.headlineText);
        headlineTextView.setText(title);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, getRequestMap());
                showDialog();
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }
        });

        webView.loadUrl(url, getRequestMap());

        rootLayout.removeAllViews();
        rootLayout.addView(webviewLayout);
    }

    private HashMap<String, String> getRequestMap() {
        HashMap<String, String> usernamePassword = new HashMap<String, String>(1);
        try {
            usernamePassword
                    .put("Authorization",
                            "Basic "
                                    + Base64.encodeToString((username() + ":" + password()).getBytes("UTF-8"),
                                            Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return usernamePassword;
    }

    private void showDialog() {
        dialog = ProgressDialog.show(questionnaire.getActivity(), "Henter", "Vent venligst...", true);
    }

    private String username() {
        return Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME);
    }

    private String password() {
        return Util.getStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD);
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
    }

}
