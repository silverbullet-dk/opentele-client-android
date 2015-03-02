package dk.silverbullet.telemed.lowlevel;


import android.content.Context;
import dk.silverbullet.telemed.rest.client.ServerInformation;
import dk.silverbullet.telemed.rest.client.lowlevel.HttpHeaderBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class HttpHeaderBuilderTest {

    private Context mockContext;
    private ServerInformation serverInformation;

    @Before
    public void setupMocks() {
        mockContext = mock(Context.class);
        serverInformation = mock(ServerInformation.class);

        when(serverInformation.getContext()).thenReturn(mockContext);
        when(mockContext.getString(anyInt())).thenReturn("mock client version");

        when(serverInformation.getUserName()).thenReturn("NancyAnn");
        when(serverInformation.getPassword()).thenReturn("abcd1234");

    }

    @Test
    public void willAlwaysAddClientVersionHeader() {
        HttpGet result = new HttpGet("http://www.example.org");

        new HttpHeaderBuilder(result, serverInformation);

        Header[] headers = result.getAllHeaders();
        assertEquals("Client-version", headers[0].getName());
        assertEquals("mock client version", headers[0].getValue());
    }

    @Test
    public void willAlwaysAddUserAgentHeader() {
        HttpGet result = new HttpGet("http://www.example.org");

        new HttpHeaderBuilder(result, serverInformation);

        Header[] headers = result.getAllHeaders();
        assertEquals("User-Agent", headers[1].getName());
    }

    @Test
    public void canSetAcceptTypeJSON() {
        HttpGet result = new HttpGet("http://www.example.org");
        new HttpHeaderBuilder(result, serverInformation).withAcceptTypeJSON();

        Header[] headers = result.getAllHeaders();
        assertEquals("Accept", headers[2].getName());
        assertEquals("application/json", headers[2].getValue());

    }

    @Test
    public void canSetAcceptTypeOctetStream() {
        HttpGet result = new HttpGet("http://www.example.org");
        new HttpHeaderBuilder(result, serverInformation).withAcceptTypeOctetStream();

        Header[] headers = result.getAllHeaders();
        assertEquals("Accept", headers[2].getName());
        assertEquals("application/octet-stream", headers[2].getValue());
    }

    @Test
    public void canSetContentTypeJSON() {
        HttpGet result = new HttpGet("http://www.example.org");
        new HttpHeaderBuilder(result, serverInformation).withContentTypeJSON();

        Header[] headers = result.getAllHeaders();
        assertEquals("Content-type", headers[2].getName());
        assertEquals("application/json", headers[2].getValue());
    }

    @Test
    public void canSetAuthenticationHeaders() {
        HttpGet result = new HttpGet("http://www.example.org");
        new HttpHeaderBuilder(result, serverInformation).withAuthentication();

        Header[] headers = result.getAllHeaders();
        assertEquals("Authorization", headers[2].getName());

        String base64Encoded = new String(Base64.encodeBase64("NancyAnn:abcd1234".getBytes()));
        assertEquals("Basic " + base64Encoded, headers[2].getValue());
    }

    @Test
    public void lastSetHeaderWins() {
        HttpGet result = new HttpGet("http://www.example.org");
        new HttpHeaderBuilder(result, serverInformation).withAcceptTypeJSON();
        new HttpHeaderBuilder(result, serverInformation).withAcceptTypeOctetStream();

        Header[] headers = result.getAllHeaders();
        assertEquals("Accept", headers[2].getName());
        assertEquals("application/octet-stream", headers[2].getValue());

    }
}
