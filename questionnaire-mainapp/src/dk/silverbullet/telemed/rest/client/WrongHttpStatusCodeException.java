package dk.silverbullet.telemed.rest.client;

import org.apache.http.HttpResponse;

public class WrongHttpStatusCodeException extends RestException {
    private final HttpResponse httpResponse;

    public WrongHttpStatusCodeException(String message, HttpResponse httpResponse) {
        super(message);
        this.httpResponse = httpResponse;
    }

    public HttpResponse getResponse() {
        return httpResponse;
    }

    public int getStatusCode() {
        return httpResponse.getStatusLine().getStatusCode();
    }

    public String getReason() {
        return httpResponse.getStatusLine().getReasonPhrase();
    }
}
