package mcmaster.reporting.reports;

/**
 * Enum representing the possible bucketer time intervals.
 */
public enum BucketerType {
  HOUR,
  DAY,
  WEEK,
  MONTH;

  public String toString() {
    return name().toLowerCase();
  }
}
