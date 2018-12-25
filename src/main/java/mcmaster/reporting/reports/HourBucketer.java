package mcmaster.reporting.reports;

import org.joda.time.DateTime;

/**
 * Bucketer which accumulates data into buckets by hour.
 */
class HourBucketer<V> extends Bucketer<V> {
  DateTime getKey(DateTime dateTime) {
    return dateTime.hourOfDay().roundFloorCopy();
  }

  DateTime getEnd(DateTime start) {
    return start.plusHours(1);
  }
}
