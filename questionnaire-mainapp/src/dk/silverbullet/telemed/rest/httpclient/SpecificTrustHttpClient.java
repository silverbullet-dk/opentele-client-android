package dk.silverbullet.telemed.rest.httpclient;

import android.content.Context;
import android.util.Log;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.R;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

class SpecificTrustHttpClient extends DefaultHttpClient {
    private final Context context;

    public SpecificTrustHttpClient(Context context) {
        this.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            Certificate certificate;
            InputStream certificateInputStream = context.getResources().openRawResource(R.raw.certs);
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                certificate = certificateFactory.generateCertificate(certificateInputStream);
                Log.e("CERT", "ca=" + ((X509Certificate) certificate).getSubjectDN());
            } finally {
                certificateInputStream.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

            return new SSLSocketFactory(keyStore);
        } catch (Exception e) {
            OpenTeleApplication.instance().logException(e);
            throw new RuntimeException(e);
        }
    }
}