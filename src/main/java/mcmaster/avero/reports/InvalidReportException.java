package mcmaster.avero.reports;

/**
 * Exception thrown when we find an error in generating a report.
 */
class InvalidReportException extends RuntimeException {

  InvalidReportException(String message) {
    super(message);
  }
}
