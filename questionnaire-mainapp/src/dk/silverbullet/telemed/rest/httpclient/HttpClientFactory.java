package dk.silverbullet.telemed.rest.httpclient;

import android.content.Context;
import android.os.Build;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientFactory {

    public static HttpClient createHttpClient(Context context) {
        if(Build.VERSION.SDK_INT == 17 /* Android 4.2, which does not trust our production certificates */) {
            return new SpecificTrustHttpClient(context);
        } else {
            return new DefaultHttpClient();
        }

    }
}
