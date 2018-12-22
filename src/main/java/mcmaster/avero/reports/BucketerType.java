package mcmaster.avero.reports;

public enum BucketerType {
  HOUR,
  DAY,
  WEEK,
  MONTH;

  public String toString() {
    return name().toLowerCase();
  }
}
