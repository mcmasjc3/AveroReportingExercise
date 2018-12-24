package mcmaster.avero.reports;

import org.joda.time.DateTime;

/**
 * Bucketer which accumulates data into buckets by month.
 */
class MonthBucketer<V> extends Bucketer<V> {
  DateTime getKey(DateTime dateTime) {
    return dateTime.monthOfYear().roundFloorCopy();
  }

  DateTime getEnd(DateTime start) {
    return start.plusMonths(1);
  }
}
