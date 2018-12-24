package mcmaster.avero.reports;

import org.joda.time.DateTime;

/**
 * Bucketer which accumulates data into buckets by week.
 */
class WeekBucketer<V> extends Bucketer<V> {
  DateTime getKey(DateTime dateTime) {
    return dateTime.weekOfWeekyear().roundFloorCopy();
  }

  DateTime getEnd(DateTime start) {
    return start.plusWeeks(1);
  }
}
