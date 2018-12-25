package mcmaster.reporting.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import fi.iki.elonen.NanoHTTPD;
import mcmaster.reporting.reports.BucketerType;
import mcmaster.reporting.reports.EmployeeGrossSalesReport;
import mcmaster.reporting.reports.FoodCostPercentageReport;
import mcmaster.reporting.reports.InvalidReportException;
import mcmaster.reporting.reports.JsonParseException;
import mcmaster.reporting.reports.LaborCostPercentageReport;
import mcmaster.reporting.reports.ReportGenerator;
import mcmaster.reporting.reports.ReportType;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.iki.elonen.NanoHTTPD.Response.Status.BAD_REQUEST;
import static fi.iki.elonen.NanoHTTPD.Response.Status.INTERNAL_ERROR;
import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

class ReportServer extends NanoHTTPD {
  private static final String SHUTDOWN_PATH = "/quitquitquit";
  private static final String REPORTING_PATH = "/reporting";
  private static final String FAVICON_PATH = "/favicon.ico";
  private static final String BUSINESS_ID = "business_id";
  private static final String REPORT = "report";
  private static final String TIME_INTERVAL = "timeInterval";
  private static final String START = "start";
  private static final String END = "end";
  private static final Set<String> VALID_PARMS =
      ImmutableSet.of(BUSINESS_ID, REPORT, TIME_INTERVAL, START, END);

  private final Injector injector;

  @Inject
  ReportServer(@PortNumber int port, Injector injector) {
    super(port);
    this.injector = injector;
    try {
      start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    } catch (IOException e) {
      System.out.println("Unable to start ReportServer");
      e.printStackTrace();
    }
    System.out.println(String.format("ReportServer started on port %d", port));
  }

  public static void main(String[] args) {
    final int port;
    try {
      port = (args.length == 0) ? 8080 : Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.out.println(String.format("Port number %s must be an integer", args[0]));
      return;
    }
    /* This throws {@link PosDataException} if we are unable to fetch the {@link DomainData}. */
    Injector injector =
        Guice.createInjector(
            new ReportModule(),
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(Integer.class).annotatedWith(PortNumber.class).toInstance(port);
              }
            });
    injector.getInstance(ReportServer.class);
  }

  @Override
  public Response serve(IHTTPSession session) {
    String path = session.getUri();
    if (path.equals(SHUTDOWN_PATH)) {
      System.out.println("Shutting down");
      closeAllConnections();
      // This causes a SocketException and a stack trace.  I can't figure out how to stop that.
      stop();
    }
    if (path.equals(FAVICON_PATH)) {
      return createResponse(OK, ImmutableList.of());
    }
    if (!path.equals(REPORTING_PATH)) {
      return createUserErrorResponse(ImmutableList.of(String.format("Invalid request: %s", path)));
    }
    ServerParms serverParms = processParms(session);
    if (!serverParms.errorMessages.isEmpty()) {
      return createUserErrorResponse(serverParms.errorMessages);
    }

    ReportGenerator reportGenerator = null;
    switch (serverParms.reportType) {
      case EGS:
        reportGenerator = injector.getInstance(EmployeeGrossSalesReport.class);
        break;
      case FCP:
        reportGenerator = injector.getInstance(FoodCostPercentageReport.class);
        break;
      case LCP:
        reportGenerator = injector.getInstance(LaborCostPercentageReport.class);
        break;
      default: // Cannot happen unless we add a new ReportType
    }

    String report;
    try {
      report =
          reportGenerator.generate(
              serverParms.businessId,
              new Interval(serverParms.startTime, serverParms.endTime),
              serverParms.reportInterval);
    } catch (InvalidReportException e) {
      return createUserErrorResponse(ImmutableList.of(e.getMessage()));
    } catch (JsonParseException e) {
      return createSystemErrorResponse(
          ImmutableList.of("Unable to parse JSON report", parseStackTrace(e)));
    }
    return createSuccessResponse(report);
  }

  private String parseStackTrace(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.getCause().printStackTrace(pw);
    return sw.toString();
  }

  private ServerParms processParms(IHTTPSession session) {
    Map<String, String> parms = session.getParms();
    ServerParms result = new ServerParms();
    result.errorMessages.addAll(checkMissingParms(parms));
    result.errorMessages.addAll(checkInvalidParms(parms));
    result.businessId = parms.get(BUSINESS_ID);
    if (parms.containsKey(REPORT)) {
      try {
        result.reportType = ReportType.valueOf(parms.get(REPORT).toUpperCase());
      } catch (IllegalArgumentException e) {
        result.errorMessages.add(
            String.format("Invalid reportGenerator type: %s", parms.get(REPORT)));
      }
    }
    if (parms.containsKey(TIME_INTERVAL)) {
      try {
        result.reportInterval = BucketerType.valueOf(parms.get(TIME_INTERVAL).toUpperCase());
      } catch (IllegalArgumentException e) {
        result.errorMessages.add(
            String.format("Invalid time interval: %s", parms.get(TIME_INTERVAL)));
      }
    }

    if (parms.containsKey(START)) {
      try {
        result.startTime = DateTime.parse(parms.get(START));
      } catch (IllegalArgumentException e) {
        result.errorMessages.add(String.format("Invalid start time: %s", parms.get(START)));
      }
    }

    if (parms.containsKey(END)) {
      try {
        result.endTime = DateTime.parse(parms.get(END));
      } catch (IllegalArgumentException e) {
        result.errorMessages.add(String.format("Invalid end time: %s", parms.get(END)));
      }
    }
    if (result.startTime != null
        && result.endTime != null
        && result.startTime.isAfter(result.endTime)) {
      result.errorMessages.add(
          String.format("Start time: %s is after end time %s", parms.get(START), parms.get(END)));
    }
    return result;
  }

  private List<String> checkMissingParms(Map<String, String> parms) {
    List<String> result = new ArrayList<>();
    if (!parms.containsKey(BUSINESS_ID)) {
      result.add(String.format("Missing parameter: %s", BUSINESS_ID));
    }
    if (!parms.containsKey(REPORT)) {
      result.add(String.format("Missing parameter: %s", REPORT));
    }
    if (!parms.containsKey(TIME_INTERVAL)) {
      result.add(String.format("Missing parameter: %s", TIME_INTERVAL));
    }
    if (!parms.containsKey(START)) {
      result.add(String.format("Missing parameter: %s", START));
    }
    if (!parms.containsKey(END)) {
      result.add(String.format("Missing parameter: %s", END));
    }
    return result;
  }

  private List<String> checkInvalidParms(Map<String, String> parms) {
    List<String> result = new ArrayList<>();
    parms
        .keySet()
        .forEach(
            s -> {
              if (!VALID_PARMS.contains(s)) {
                result.add(String.format("Invalid parameter: %s", s));
              }
            });
    return result;
  }

  private Response createSuccessResponse(String report) {
    return createResponse(OK, ImmutableList.of(report));
  }

  private Response createUserErrorResponse(List<String> messages) {
    return createResponse(BAD_REQUEST, messages);
  }

  private Response createSystemErrorResponse(List<String> messages) {
    return createResponse(INTERNAL_ERROR, messages);
  }

  private Response createResponse(Response.Status status, List<String> messages) {
    StringBuilder responseContent = new StringBuilder("<html>");
    messages.forEach((m) -> responseContent.append(String.format("<p>%s</p>", m)));
    responseContent.append("</html>");
    return newFixedLengthResponse(status, MIME_HTML, responseContent.toString());
  }

  private class ServerParms {
    String businessId;
    ReportType reportType;
    BucketerType reportInterval;
    DateTime startTime;
    DateTime endTime;
    final List<String> errorMessages = new ArrayList<>();
  }

  @BindingAnnotation
  @Retention(RUNTIME)
  @interface PortNumber {
  }
}
