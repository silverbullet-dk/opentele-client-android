package dk.silverbullet.telemed.questionnaire.node;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

public class WebViewNode extends IONode {

    private static final String TAG = Util.getTag(WebViewNode.class);
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
        Context context = questionnaire.getContext();
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View webviewLayout = inflater.inflate(R.layout.webview_node, null);
        webView = (WebView) webviewLayout.findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);

        CookieSyncManager.createInstance(context);
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

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                handler.proceed(username(), password());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(Build.VERSION.SDK_INT == 17 /* Android 4.2, which does not trust our production certificates */) {
                    if (isCertificateTrusted(error)) {
                        handler.proceed();
                    } else {
                        handler.cancel();
                    }
                } else {
                    super.onReceivedSslError(view, handler, error);
                }
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
        dialog = ProgressDialog.show(questionnaire.getContext(), Util.getString(R.string.web_view_fetching, questionnaire), Util.getString(R.string.default_please_wait, questionnaire), true);
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

    private boolean isCertificateTrusted(SslError error) {
        try {
            X509Certificate x509Certificate = getCertificateFromError(error);
            KeyStore keyStore = createKeyStore();
            X509TrustManager x590TrustManager = getTrustManager(keyStore);

            X509Certificate[] chain = new X509Certificate[]{x509Certificate};

            x590TrustManager.checkServerTrusted(chain, TrustManagerFactory.getDefaultAlgorithm());
            //checkServerTrusted will throw a CertificateException so if we get this far the certificate is trusted
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "Cert validation failed", ex);
            return false;
        }
    }

    private X509TrustManager getTrustManager(KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore createKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        InputStream certificateInputStream = questionnaire.getContext().getResources().openRawResource(R.raw.certs);
        Certificate certificate;
        try {
            certificate = certificateFactory.generateCertificate(certificateInputStream);
        } finally {
            certificateInputStream.close();
        }

        keyStore.setCertificateEntry("ca", certificate);

        return keyStore;
    }

    private X509Certificate getCertificateFromError(SslError error) throws NoSuchFieldException, IllegalAccessException {
        SslCertificate sslCertificate = error.getCertificate();
        Field mX509CertificateField =  sslCertificate.getClass().getDeclaredField("mX509Certificate");
        mX509CertificateField.setAccessible(true);

        return (X509Certificate) mX509CertificateField.get(sslCertificate);
    }
}
