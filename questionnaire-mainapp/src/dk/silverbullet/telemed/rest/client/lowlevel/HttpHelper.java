package dk.silverbullet.telemed.rest.client.lowlevel;

import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.rest.client.ServerInformation;
import dk.silverbullet.telemed.rest.client.WrongHttpStatusCodeException;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
        Util.setHeaders(result, serverInformation);
        return result;
    }

    public static HttpPost createHttpPostForPathWithEntity(ServerInformation serverInformation, String path, HttpEntity entity) {
        String url = urlForPath(serverInformation, path);

        HttpPost result = new HttpPost(url);
        Util.setHeaders(result, serverInformation);
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
