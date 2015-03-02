package dk.silverbullet.telemed.rest.client.lowlevel;

import android.os.Build;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.rest.client.ServerInformation;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.auth.BasicScheme;

import java.util.Locale;

public class HttpHeaderBuilder {
    private HttpRequestBase request;
    private ServerInformation serverInformation;

    public HttpHeaderBuilder(HttpRequestBase request, ServerInformation serverInformation) {
        this.serverInformation = serverInformation;
        //always add client version
        //always set user-agent
        String clientVersion = serverInformation.getContext().getString(R.string.client_version);
        request.setHeader("Client-version", clientVersion);
        request.setHeader("User-Agent", userAgentString(clientVersion));

        this.request = request;
    }

    public HttpHeaderBuilder withAuthentication() {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(serverInformation.getUserName(), serverInformation.getPassword());
        request.setHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

        return this;
    }

    public HttpHeaderBuilder withContentTypeJSON() {
        request.setHeader("Content-type", "application/json");
        return this;
    }

    public HttpHeaderBuilder withAcceptTypeJSON() {
        request.setHeader("Accept", "application/json");

        return this;
    }

    public HttpHeaderBuilder withAcceptTypeOctetStream() {
        request.setHeader("Accept", "application/octet-stream");

        return this;
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
