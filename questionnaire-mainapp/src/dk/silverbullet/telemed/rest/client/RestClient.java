package dk.silverbullet.telemed.rest.client;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class RestClient {
    public static String get(Questionnaire questionnaire, String path) throws RestException {
        String url = urlForPath(questionnaire, path);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        Util.setHeaders(httpGet, questionnaire);

        try {
            return httpClient.execute(httpGet, new BasicResponseHandler());
        } catch (IOException e) {
            throw new RestException("Could not GET from '" + url + "'", e);
        }
    }

    public static <T> T postJson(Questionnaire questionnaire, String path, Object object, Class<T> resultClass) throws RestException {
        String url = urlForPath(questionnaire, path);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        Util.setHeaders(httpPost, questionnaire);
        httpPost.setEntity(createHttpEntityFromSerializedObject(object));

        try {
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RestException("Unexpected status code '" + statusCode + "' when POSTing to url '" + url + "'");
            }

            return Json.parse(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), resultClass);
        } catch (IOException e) {
            throw new RestException("Could not POST to '" + url + "'", e);
        }
    }

    private static String urlForPath(Questionnaire questionnaire, String path) {
        return Util.getServerUrl(questionnaire) + path + "?lang=" + Locale.getDefault().getLanguage();
    }

    private static HttpEntity createHttpEntityFromSerializedObject(Object object) {
        String json = Json.print(object);
        try {
            return new StringEntity(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Never, ever happens
            throw new RuntimeException("Could not create HTTP entity", e);
        }
    }
}
