package dk.silverbullet.telemed.rest.client;

import android.graphics.Bitmap;
import dk.silverbullet.telemed.rest.client.lowlevel.HttpHeaderBuilder;
import dk.silverbullet.telemed.rest.client.lowlevel.HttpHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;

/**
 * Methods to perform simple REST calls to the server, handling JSON serialization and deserialization. Hopefully the
 * pain of having to handle HTTP status codes etc. is limited by using these methods.
 *
 * If the server returns an HTTP status code other than 200, a WrongHttpStatusCodeException is thrown, from which the
 * response code etc. can be fetched.
 *
 * If something else goes wrong (i.e., some of Java's methods throw an IOException), the more general RestException is
 * thrown. Usually the client cannot do anything about that, except log the error, fail the operation, and tell the
 * user that something went wrong.
 */
public class RestClient {
    public static String getString(ServerInformation serverInformation, String path) throws RestException {
        HttpGet httpGet = HttpHelper.createHttpGetForPath(serverInformation, path);

        try {
            HttpResponse response = HttpHelper.get(serverInformation, httpGet);
            return HttpHelper.parseResponseAsString(response);
        } catch (IOException e) {
            throw new RestException("Could not GET from '" + httpGet.getURI() + "'", e);
        }
    }

    public static String getStringFromUnauthenticatedResource(ServerInformation serverInformation, String path) throws RestException {
        HttpGet httpGet = HttpHelper.createHttpGetForPathWithoutAuthentication(serverInformation, path);

        try {
            HttpResponse response = HttpHelper.get(serverInformation, httpGet);
            return HttpHelper.parseResponseAsString(response);
        } catch (IOException e) {
            throw new RestException("Could not GET from '" + httpGet.getURI() + "'", e);
        }
    }





    public static<T> T getJson(ServerInformation serverInformation, String path, Class<T> resultClass) throws RestException {
        HttpGet httpGet = HttpHelper.createHttpGetForPath(serverInformation, path);

        try {
            HttpResponse response = HttpHelper.get(serverInformation, httpGet);
            return HttpHelper.parseResponseAsJson(response, resultClass);
        } catch (IOException e) {
            throw new RestException("Could not GET from '" + httpGet.getURI() + "'", e);
        }
    }

    public static Bitmap getImage(ServerInformation serverInformation, String path) throws RestException {
        HttpGet httpGet = HttpHelper.createHttpGetForPath(serverInformation, path);

        new HttpHeaderBuilder(httpGet, serverInformation).withAcceptTypeOctetStream();

        try {
            HttpResponse response = HttpHelper.get(serverInformation, httpGet);
            return HttpHelper.parseResponseAsImage(response);
        } catch (IOException e) {
            throw new RestException("Could not GET from '" + httpGet.getURI() + "'", e);
        }
    }

    public static void postJson(ServerInformation serverInformation, String path, Object object) throws RestException {
        HttpPost httpPost = HttpHelper.createHttpPostForPathWithEntity(serverInformation, path, HttpHelper.createHttpEntityFromSerializedObject(object));

        try {
            HttpHelper.post(serverInformation, httpPost);
        } catch (IOException e) {
            throw new RestException("Could not POST to '" + httpPost.getURI() + "'", e);
        }
    }

    public static <T> T postJson(ServerInformation serverInformation, String path, Object object, Class<T> resultClass) throws RestException {
        HttpPost httpPost = HttpHelper.createHttpPostForPathWithEntity(serverInformation, path, HttpHelper.createHttpEntityFromSerializedObject(object));

        try {
            HttpResponse response = HttpHelper.post(serverInformation, httpPost);
            return HttpHelper.parseResponseAsJson(response, resultClass);
        } catch (IOException e) {
            throw new RestException("Could not POST to '" + httpPost.getURI() + "'", e);
        }
    }
}
