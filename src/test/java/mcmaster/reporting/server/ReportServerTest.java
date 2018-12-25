package mcmaster.reporting.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import mcmaster.reporting.posfetch.data.DomainData;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static com.google.common.truth.Truth.assertThat;
import static fi.iki.elonen.NanoHTTPD.Response.Status.BAD_REQUEST;
import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static org.mockito.Mockito.when;

public class ReportServerTest {
  private static final ReportModule REPORT_MODULE =
      new ReportModule() {
        /** Overridden to make tests faster. We only fetch {@link DomainData} when the test injects it. */
        @Override
        protected void configureDomainData() {
          bind(DomainData.class).in(Scopes.SINGLETON);
        }
      };
  @Rule
  public MockitoRule rule = MockitoJUnit.rule();
  private static final int PORT = 8080;

  @Mock
  private IHTTPSession session;

  private ReportServer server;

  @Before
  public void setUp() {
    Injector injector = Guice.createInjector(REPORT_MODULE);
    server = new ReportServer(PORT, injector);
    when(session.getUri()).thenReturn("/reporting");
  }

  @After
  public void tearDown() {
    server.stop();
  }

  @Test
  public void testServerStartup() {
    assertThat(server.isAlive()).isTrue();
    assertThat(server.getListeningPort()).isEqualTo(PORT);
  }

  @Test
  public void testServerInvalidRequest() throws Exception {
    when(session.getUri()).thenReturn("bad");
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content).contains("Invalid request: bad");
  }

  @Test
  public void testServerMissingParms() throws Exception {
    when(session.getParms()).thenReturn(ImmutableMap.of());
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content).contains("Missing parameter: business_id");
    assertThat(content).contains("Missing parameter: report");
    assertThat(content).contains("Missing parameter: timeInterval");
    assertThat(content).contains("Missing parameter: start");
    assertThat(content).contains("Missing parameter: end");
  }

  @Test
  public void testServerInvalidReport() throws Exception {
    when(session.getParms()).thenReturn(ImmutableMap.of("report", "BAD"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content).contains("Invalid reportGenerator type: BAD");
  }

  @Test
  public void testServerInvalidTimeInterval() throws Exception {
    when(session.getParms()).thenReturn(ImmutableMap.of("timeInterval", "BAD"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content).contains("Invalid time interval: BAD");
  }

  @Test
  public void testServerInvalidStart() throws Exception {
    when(session.getParms()).thenReturn(ImmutableMap.of("start", "BAD"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content).contains("Invalid start time: BAD");
  }

  @Test
  public void testServerInvalidEnd() throws Exception {
    when(session.getParms()).thenReturn(ImmutableMap.of("end", "BAD"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content).contains("Invalid end time: BAD");
  }

  @Test
  public void testServerStartAfterEnd() throws Exception {
    when(session.getParms())
        .thenReturn(
            ImmutableMap.of(
                "start", "2018-12-20T12:00:00.000Z", "end", "2018-12-19T12:00:00.000Z"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content)
        .contains(
            "Start time: 2018-12-20T12:00:00.000Z is after end time 2018-12-19T12:00:00.000Z");
  }

  @Test
  public void testServerInvalidBusiness() throws Exception {
    when(session.getParms())
        .thenReturn(
            ImmutableMap.of(
                "business_id",
                "bad",
                "report",
                "EGS",
                "timeInterval",
                "HOUR",
                "start",
                "2018-12-19T12:00:00.000Z",
                "end",
                "2018-12-20T12:00:00.000Z"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content).contains("Business bad not found");
  }

  @Test
  public void testServerLcpReportByHour() throws Exception {
    when(session.getParms())
        .thenReturn(
            ImmutableMap.of(
                "business_id",
                "f21c2579-b95e-4a5b-aead-a3cf9d60d43b",
                "report",
                "LCP",
                "timeInterval",
                "hour",
                "start",
                "2018-05-03T15:00:00.000Z",
                "end",
                "2018-05-03T18:00:00.000Z"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(OK);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content)
        .isEqualTo(
            "<html><p>{\n"
                + "  \"report\": \"LCP\",\n"
                + "  \"timeInterval\": \"hour\",\n"
                + "  \"data\": []\n"
                + "}</p></html>");
  }

  @Test
  public void testServerLcpReportByDay() throws Exception {
    when(session.getParms())
        .thenReturn(
            ImmutableMap.of(
                "business_id",
                "f21c2579-b95e-4a5b-aead-a3cf9d60d43b",
                "report",
                "LCP",
                "timeInterval",
                "day",
                "start",
                "2018-05-01T00:00:00.000Z",
                "end",
                "2018-05-02T00:00:00.000Z"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(OK);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content)
        .isEqualTo(
            "<html><p>{\n"
                + "  \"report\": \"LCP\",\n"
                + "  \"timeInterval\": \"day\",\n"
                + "  \"data\": []\n"
                + "}</p></html>");
  }

  @Test
  public void testServerFcpReport() throws Exception {
    when(session.getParms())
        .thenReturn(
            ImmutableMap.of(
                "business_id",
                "f21c2579-b95e-4a5b-aead-a3cf9d60d43b",
                "report",
                "FCP",
                "timeInterval",
                "hour",
                "start",
                "2018-05-03T15:00:00.000Z",
                "end",
                "2018-05-03T18:00:00.000Z"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(OK);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content)
        .isEqualTo(
            "<html><p>{\n"
                + "  \"report\": \"FCP\",\n"
                + "  \"timeInterval\": \"hour\",\n"
                + "  \"data\": []\n"
                + "}</p></html>");
  }

  @Test
  public void testServerEgsReport() throws Exception {
    when(session.getParms())
        .thenReturn(
            ImmutableMap.of(
                "business_id",
                "f21c2579-b95e-4a5b-aead-a3cf9d60d43b",
                "report",
                "EGS",
                "timeInterval",
                "hour",
                "start",
                "2018-05-03T15:00:00.000Z",
                "end",
                "2018-05-03T18:00:00.000Z"));
    Response response = server.serve(session);
    assertThat(response.getStatus()).isEqualTo(OK);
    InputStream data = response.getData();
    String content = convertData(data);
    assertThat(content)
        .isEqualTo(
            "<html><p>{\n"
                + "  \"report\": \"EGS\",\n"
                + "  \"timeInterval\": \"hour\",\n"
                + "  \"data\": []\n"
                + "}</p></html>");
  }

  private String convertData(InputStream data) throws IOException {
    String content;
    try (final Reader reader = new InputStreamReader(data)) {
      content = CharStreams.toString(reader);
    }
    return content;
  }
}
