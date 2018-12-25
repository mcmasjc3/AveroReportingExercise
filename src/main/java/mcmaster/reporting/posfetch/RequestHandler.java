package mcmaster.reporting.posfetch;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.io.CharStreams;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Class that passes HTTP requests to the POS system and parses the result into a JSON String. */
class RequestHandler {
  private static final String ACCESS_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1NDQ0NTgwODMsImV4cCI6MTU0NzA1MDA4M"
          + "30.fQ8h7yK2zFj1WRqGnPEdu87VjbMmATKy2kaOOhcGBAs";
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private final HttpTransport httpTransport;

  @Inject
  RequestHandler(HttpTransport httpTransport) {
    this.httpTransport = httpTransport;
  }

  String sendRequest(GenericUrl url) throws IOException {
    HttpRequestFactory requestFactory =
        httpTransport.createRequestFactory(
            initializer -> initializer.setParser(new JsonObjectParser(JSON_FACTORY)));
    HttpRequest request = requestFactory.buildGetRequest(url);
    request.getHeaders().setAuthorization(ACCESS_TOKEN);
    InputStream contentStream = request.execute().getContent();
    return CharStreams.toString(new InputStreamReader(contentStream, StandardCharsets.UTF_8));
  }
}
