package mcmaster.avero.reports;

import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Tests for {@link Bucketer}
 */
public class BucketerTest {
  @Test
  public void hourBucket() {
    Bucketer<Integer> bucketer = Bucketer.create(BucketerType.HOUR);
    DateTime date1 = new DateTime(2018, 12, 19, 12, 2);
    DateTime date2 = new DateTime(2018, 12, 19, 12, 59);
    DateTime date3 = new DateTime(2018, 12, 19, 15, 2);
    bucketer.add(date1, 1);
    bucketer.add(date2, 2);
    bucketer.add(date3, 3);
    ImmutableMap<DateTime, Bucketer.Bucket<Integer>> buckets = bucketer.getBuckets();
    assertThat(buckets.size()).isEqualTo(2);
    DateTime expectedStart1 = new DateTime(2018, 12, 19, 12, 0);
    Bucketer.Bucket<Integer> bucket1 = buckets.get(expectedStart1);
    assertThat(bucket1.getStart()).isEqualTo(expectedStart1);
    assertThat(bucket1.getEnd()).isEqualTo(new DateTime(2018, 12, 19, 13, 0));
    assertThat(bucket1.getValues()).containsExactly(1, 2);
    DateTime expectedStart2 = new DateTime(2018, 12, 19, 15, 0);
    Bucketer.Bucket<Integer> bucket2 = buckets.get(expectedStart2);
    assertThat(bucket2.getStart()).isEqualTo(expectedStart2);
    assertThat(bucket2.getEnd()).isEqualTo(new DateTime(2018, 12, 19, 16, 0));
    assertThat(bucket2.getValues()).containsExactly(3);
  }

  @Test
  public void dayBucket() {
    Bucketer<Integer> bucketer = Bucketer.create(BucketerType.DAY);
    DateTime date1 = new DateTime(2018, 12, 19, 12, 2);
    DateTime date2 = new DateTime(2018, 12, 19, 14, 59);
    DateTime date3 = new DateTime(2018, 12, 21, 15, 2);
    bucketer.add(date1, 1);
    bucketer.add(date2, 2);
    bucketer.add(date3, 3);
    ImmutableMap<DateTime, Bucketer.Bucket<Integer>> buckets = bucketer.getBuckets();
    assertThat(buckets.size()).isEqualTo(2);
    DateTime expectedStart1 = new DateTime(2018, 12, 19, 0, 0);
    Bucketer.Bucket<Integer> bucket1 = buckets.get(expectedStart1);
    assertThat(bucket1.getStart()).isEqualTo(expectedStart1);
    assertThat(bucket1.getEnd()).isEqualTo(new DateTime(2018, 12, 20, 0, 0));
    assertThat(bucket1.getValues()).containsExactly(1, 2);
    DateTime expectedStart2 = new DateTime(2018, 12, 21, 0, 0);
    Bucketer.Bucket<Integer> bucket2 = buckets.get(expectedStart2);
    assertThat(bucket2.getStart()).isEqualTo(expectedStart2);
    assertThat(bucket2.getEnd()).isEqualTo(new DateTime(2018, 12, 22, 0, 0));
    assertThat(bucket2.getValues()).containsExactly(3);
  }

  @Test
  public void weekBucket() {
    Bucketer<Integer> bucketer = Bucketer.create(BucketerType.WEEK);
    DateTime date1 = new DateTime(2018, 12, 19, 12, 2);
    DateTime date2 = new DateTime(2018, 12, 21, 14, 59);
    DateTime date3 = new DateTime(2018, 12, 24, 15, 2);
    bucketer.add(date1, 1);
    bucketer.add(date2, 2);
    bucketer.add(date3, 3);
    ImmutableMap<DateTime, Bucketer.Bucket<Integer>> buckets = bucketer.getBuckets();
    assertThat(buckets.size()).isEqualTo(2);
    DateTime expectedStart1 = new DateTime(2018, 12, 17, 0, 0);
    Bucketer.Bucket<Integer> bucket1 = buckets.get(expectedStart1);
    assertThat(bucket1.getStart()).isEqualTo(expectedStart1);
    assertThat(bucket1.getEnd()).isEqualTo(new DateTime(2018, 12, 24, 0, 0));
    assertThat(bucket1.getValues()).containsExactly(1, 2);
    DateTime expectedStart2 = new DateTime(2018, 12, 24, 0, 0);
    Bucketer.Bucket<Integer> bucket2 = buckets.get(expectedStart2);
    assertThat(bucket2.getStart()).isEqualTo(expectedStart2);
    assertThat(bucket2.getEnd()).isEqualTo(new DateTime(2018, 12, 31, 0, 0));
    assertThat(bucket2.getValues()).containsExactly(3);
  }

  @Test
  public void monthBucket() {
    Bucketer<Integer> bucketer = Bucketer.create(BucketerType.MONTH);
    DateTime date1 = new DateTime(2018, 12, 19, 12, 2);
    DateTime date2 = new DateTime(2018, 12, 21, 14, 59);
    DateTime date3 = new DateTime(2019, 1, 24, 15, 2);
    bucketer.add(date1, 1);
    bucketer.add(date2, 2);
    bucketer.add(date3, 3);
    ImmutableMap<DateTime, Bucketer.Bucket<Integer>> buckets = bucketer.getBuckets();
    assertThat(buckets.size()).isEqualTo(2);
    DateTime expectedStart1 = new DateTime(2018, 12, 1, 0, 0);
    Bucketer.Bucket<Integer> bucket1 = buckets.get(expectedStart1);
    assertThat(bucket1.getStart()).isEqualTo(expectedStart1);
    assertThat(bucket1.getEnd()).isEqualTo(new DateTime(2019, 1, 1, 0, 0));
    assertThat(bucket1.getValues()).containsExactly(1, 2);
    DateTime expectedStart2 = new DateTime(2019, 1, 1, 0, 0);
    Bucketer.Bucket<Integer> bucket2 = buckets.get(expectedStart2);
    assertThat(bucket2.getStart()).isEqualTo(expectedStart2);
    assertThat(bucket2.getEnd()).isEqualTo(new DateTime(2019, 2, 1, 0, 0));
    assertThat(bucket2.getValues()).containsExactly(3);
  }
}
