package mcmaster.reporting.reports;

/**
 * Exception thrown when we find an error in generating a report.
 */
public class InvalidReportException extends RuntimeException {

  InvalidReportException(String message) {
    super(message);
  }
}
