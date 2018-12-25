package mcmaster.reporting.reports;

/**
 * Exception thrown when we find an error in generating a report.
 */
public class JsonParseException extends RuntimeException {

  JsonParseException(Throwable cause) {
    super(cause);
  }
}
