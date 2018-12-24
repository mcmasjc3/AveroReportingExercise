package mcmaster.avero.reports;

import org.joda.time.DateTime;

/**
 * Bucketer which accumulates data into buckets by day.
 */
class DayBucketer<V> extends Bucketer<V> {
  DateTime getKey(DateTime dateTime) {
    return dateTime.dayOfYear().roundFloorCopy();
  }

  DateTime getEnd(DateTime start) {
    return start.plusDays(1);
  }
}
