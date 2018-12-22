package mcmaster.avero.reports;

import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class Bucketer<V> {
  private Map<DateTime, List<V>> buckets = new TreeMap<>();
  private BucketerType type;

  Bucketer(BucketerType type) {
    this.type = type;
  }

  void add(DateTime dateTime, V value) {
    DateTime key = getKey(dateTime);
    buckets.putIfAbsent(key, new ArrayList<>());
    List<V> values = buckets.get(key);
    values.add(value);
    buckets.put(key, values);
  }

  ImmutableMap<DateTime, Bucket<V>> getBuckets() {
    ImmutableMap.Builder<DateTime, Bucket<V>> result = ImmutableMap.builder();
    for (Map.Entry<DateTime, List<V>> entry : buckets.entrySet()) {
      Bucket<V> bucket = new Bucket<>();
      bucket.start = entry.getKey();
      bucket.end = getEnd(entry.getKey());
      bucket.values = entry.getValue();
      result.put(bucket.start, bucket);
    }
    return result.build();
  }

  private DateTime getKey(DateTime dateTime) {
    DateTime result = null;
    switch (type) {
      case HOUR:
        result = dateTime.hourOfDay().roundFloorCopy();
        break;
      case DAY:
        result = dateTime.dayOfYear().roundFloorCopy();
        break;
      case WEEK:
        result = dateTime.weekOfWeekyear().roundFloorCopy();
        break;
      case MONTH:
        result = dateTime.monthOfYear().roundFloorCopy();
        break;
    }
    return result;
  }

  private DateTime getEnd(DateTime start) {
    DateTime result = null;
    switch (type) {
      case HOUR:
        result = start.plusHours(1);
        break;
      case DAY:
        result = start.plusDays(1);
        break;
      case WEEK:
        result = start.plusWeeks(1);
        break;
      case MONTH:
        result = start.plusMonths(1);
        break;
    }
    return result;
  }

  static class Bucket<V> {
    DateTime start;
    DateTime end;
    List<V> values;
  }
}
