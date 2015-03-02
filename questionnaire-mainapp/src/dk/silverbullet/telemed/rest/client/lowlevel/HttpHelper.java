package dk.silverbullet.telemed.rest.client.lowlevel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.rest.client.ServerInformation;
import dk.silverbullet.telemed.rest.client.WrongHttpStatusCodeException;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Json;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.Locale;

/**
 * Various helper methods for the RestClient.
 */
public class HttpHelper {
    // The time-out in milliseconds for the underlying HTTP client
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 60000;

    public static HttpGet createHttpGetForPath(ServerInformation serverInformation, String path) {
        String url = urlForPath(serverInformation, path);

        HttpGet result = new HttpGet(url);

        new HttpHeaderBuilder(result, serverInformation)
                .withAuthentication()
                .withAcceptTypeJSON();

        return result;
    }

    public static HttpGet createHttpGetForPathWithoutAuthentication(ServerInformation serverInformation, String path) {
        String url = urlForPath(serverInformation, path);

        HttpGet result = new HttpGet(url);

        new HttpHeaderBuilder(result, serverInformation)
                .withAcceptTypeJSON();

        return result;
    }


    public static HttpPost createHttpPostForPathWithEntity(ServerInformation serverInformation, String path, HttpEntity entity) {
        String url = urlForPath(serverInformation, path);

        HttpPost result = new HttpPost(url);

        new HttpHeaderBuilder(result, serverInformation)
                .withContentTypeJSON()
                .withAcceptTypeJSON()
                .withAuthentication();

        result.setEntity(entity);
        return result;
    }

    public static HttpResponse get(ServerInformation serverInformation, HttpGet httpGet) throws IOException, RestException {
        HttpClient httpClient = createHttpClient(serverInformation);

        HttpResponse response = httpClient.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode != 200) {
            throw new WrongHttpStatusCodeException("Unexpected status code '" + statusCode + "' when GETting url '" + httpGet.getURI() + "'", response);
        }

        return response;
    }

    public static HttpResponse post(ServerInformation serverInformation, HttpPost httpPost) throws IOException, RestException {
        HttpClient httpClient = createHttpClient(serverInformation);

        HttpResponse response = httpClient.execute(httpPost);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode != 200) {
            throw new WrongHttpStatusCodeException("Unexpected status code '" + statusCode + "' when POSTing to url '" + httpPost.getURI() + "'", response);
        }

        return response;
    }

    public static HttpEntity createHttpEntityFromSerializedObject(Object object) {
        String json = Json.print(object);
        try {
            return new StringEntity(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Never, ever happens
            throw new RuntimeException("Could not create HTTP entity", e);
        }
    }

    public static <T> T parseResponseAsJson(HttpResponse response, Class<T> resultClass) throws IOException {
        InputStreamReader reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
        try {
            return Json.parse(reader, resultClass);
        } finally {
            reader.close();
        }
    }

    public static String parseResponseAsString(HttpResponse response) throws IOException {
        HttpEntity httpEntity = response.getEntity();
        return EntityUtils.toString(httpEntity);
    }

    public static Bitmap parseResponseAsImage(HttpResponse response) throws IOException {
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            //Note: Don't try to use EntityUtils.toByteArray(httpEntity) here! It has memory
            //issues!
            InputStream inputStream = null;
            try {
                inputStream = httpEntity.getContent();
                return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                httpEntity.consumeContent();
            }
        }
        return null;
    }

    //An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
    //From http://android-developers.blogspot.dk/2010/07/multithreading-for-performance.html
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    private static String urlForPath(ServerInformation serverInformation, String path) {
        return serverInformation.getServerUrl() + path + "?lang=" + Locale.getDefault().getLanguage();
    }

    private static HttpClient createHttpClient(ServerInformation serverInformation) {
        HttpClient httpClient = HttpClientFactory.createHttpClient(serverInformation.getContext());

        HttpParams httpParameters = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

        return httpClient;
    }
}
