package mcmaster.avero.reports;

import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * This is the superclass for implementations of a data bucketer.
 *
 * <p>It maintains a {@link Map} of data indexed by a {@link DateTime} that indicates the start time
 * for the bucket. The map values are a {@link List} of data items within the time range that starts
 * at the start time and ends at the end of the Bucketer's time interval. Bucket start times always
 * are aligned with the beginning of the time interval represented by the bucket.
 *
 * <p>Subclasses must implement {@link #getKey(DateTime)} and {@link #getEnd(DateTime)}.
 *
 * @param <V> the type of data accumulated in the bucket.
 */
abstract class Bucketer<V> {
  private final Map<DateTime, Bucket<V>> buckets = new TreeMap<>();

  /**
   * Factory method for creating Bucketer instances.
   *
   * <p>Throws {@link InvalidReportException} if the {@link BucketerType} is invalid. This should
   * only happen if we add a new type and don't update the factory.
   */
  static <T> Bucketer<T> create(BucketerType bucketerType) {
    switch (bucketerType) {
      case HOUR:
        return new HourBucketer<>();
      case DAY:
        return new DayBucketer<>();
      case WEEK:
        return new WeekBucketer<>();
      case MONTH:
        return new MonthBucketer<>();
      default: // Should not happen
        throw new InvalidReportException(
            String.format("Invalid BucketerType %s requested", bucketerType));
    }
  }

  /**
   * Adds a new value to a bucket.
   *
   * <p>We create a bucket key by truncating the {@link DateTime} to the beginning of the interval
   * and add the value to the {@link Bucket}.
   */
  void add(DateTime dateTime, V value) {
    DateTime key = getKey(dateTime);
    buckets.putIfAbsent(key, buildBucket(key, new ArrayList<>()));
    List<V> values = buckets.get(key).getValues();
    values.add(value);
//    buckets.put(key, values);
  }

  /**
   * Return all {@link Bucket Buckets} in the Bucketer.
   */
  ImmutableMap<DateTime, Bucket<V>> getBuckets() {
    return ImmutableMap.copyOf(buckets);
  }

  /**
   * Return a single {@link Bucket) that contains data for the given {@link DateTime}.
   *
   * <p>We use {@link Optional} to prevent a null check in the caller.
   */
  Optional<Bucket<V>> getBucket(DateTime dateTime) {
    return Optional.ofNullable(buckets.get(getKey(dateTime)));
  }

  /**
   * Implemented by subclasses to get the key for a {@link Bucket}.
   */
  abstract DateTime getKey(DateTime dateTime);

  /**
   * Implemented by subclasses to get the key for a {@link Bucket}.
   */
  abstract DateTime getEnd(DateTime start);

  private Bucket<V> buildBucket(DateTime key, List<V> value) {
    return new Bucket<>(key, getEnd(key), value);
  }

  /**
   * Represents a single bucket of values.
   */
  static class Bucket<V> {
    private final DateTime start;
    private final DateTime end;
    private final List<V> values;

    Bucket(DateTime start, DateTime end, List<V> values) {
      this.start = start;
      this.end = end;
      this.values = values;
    }

    DateTime getStart() {
      return start;
    }

    DateTime getEnd() {
      return end;
    }

    List<V> getValues() {
      return values;
    }
  }
}
