package mcmaster.avero.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import mcmaster.avero.domain.DomainData;
import mcmaster.avero.posfetch.data.Business;
import mcmaster.avero.posfetch.data.LaborEntry;
import mcmaster.avero.posfetch.data.OrderedItem;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates a food cost percentage report.
 */
class LaborCostPercentageReport
    extends AbstractReportGenerator<
    LaborCostPercentageReport.Report, LaborCostPercentageReport.Data> {
  static final String REPORT_TYPE = "LCP";

  @Inject
  LaborCostPercentageReport(DomainData domainData) {
    super(domainData, REPORT_TYPE);
  }

  /**
   * Returns the report for the percentage of labor costs in total sales for the requested {@link
   * Business} and {@link Interval}.
   */
  @Override
  Report createReport(String businessId, String timeFrame, List<Data> data) {
    return new Report(businessId, timeFrame, data);
  }

  /**
   * Returns Data items for the report.
   *
   * <p>We create {@link Bucketer Bucketers} for labor costs in {@link LaborEntry LaborEntries} and
   * prices in {@link OrderedItem OrderedItems} for the given {@link Business} and {@link Interval}.
   *
   * <p>Once we have those, we run through the OrderedItem {@link Bucketer.Bucket Buckets}. For each
   * one, we look for a matching Bucket for the LaborEntries, using a cost of zero if we don't find
   * one. Theb we do the division, creating a {@link Data} for each bucket.
   */
  List<Data> generateData(String businessId, Interval reportInterval, BucketerType bucketerType) {
    ImmutableList.Builder<Data> result = ImmutableList.builder();
    Bucketer<Integer> sales = bucketSales(businessId, reportInterval, bucketerType);
    ImmutableMap<DateTime, Bucketer.Bucket<Integer>> costs =
        bucketLaborEntries(businessId, reportInterval, bucketerType).getBuckets();
    for (Bucketer.Bucket<Integer> salesBucket : sales.getBuckets().values()) {
      BigDecimal totalSales =
          new BigDecimal(
              salesBucket.getValues().stream().collect((Collectors.summingInt((s) -> s))));
      BigDecimal totalCosts =
          (costs.containsKey(salesBucket.getStart()))
              ? new BigDecimal(
              costs
                  .get(salesBucket.getStart())
                  .getValues()
                  .stream()
                  .collect((Collectors.summingInt((s) -> s))))
              : BigDecimal.ZERO;
      result.add(
          new Data(
              new TimeFrame(salesBucket.getStart().toString(), salesBucket.getEnd().toString()),
              totalCosts
                  .divide(totalSales, 3, BigDecimal.ROUND_HALF_UP)
                  .multiply(new BigDecimal(100))
                  .setScale(1, BigDecimal.ROUND_HALF_UP)));
    }
    return result.build();
  }

  /**
   * Create a {@link Bucketer} for the given {@link BucketerType} and add the price for each {@link
   * OrderedItem}.
   */
  private Bucketer<Integer> bucketSales(
      String businessId, Interval reportInterval, BucketerType bucketerType) {
    Bucketer<Integer> bucketer = Bucketer.create(bucketerType);
    domainData
        .getOrderedItems(businessId, reportInterval)
        .stream()
        .filter((o) -> !o.isVoided())
        .forEach((o) -> bucketer.add(o.getCreatedAt(), o.getPrice()));
    return bucketer;
  }

  /**
   * Create a {@link Bucketer} for the given {@link BucketerType} and add the labor cost for each
   * {@link LaborEntry}.
   *
   * <p>Each LaborEntry represents a shift for an employee. The payRate for an employee is for a
   * single hour, not the whole shift. So, we add a bucket entry for each hour of a shift that falls
   * within the report interval. This works because the Bucketer will automatically put the labor
   * cost into the correct bucket for the BucketerType.
   */
  private Bucketer<Integer> bucketLaborEntries(
      String businessId, Interval reportInterval, BucketerType bucketerType) {
    Bucketer<Integer> bucketer = Bucketer.create(bucketerType);
    List<LaborEntry> laborEntries = domainData.getLaborEntries(businessId, reportInterval);
    for (LaborEntry entry : laborEntries) {
      DateTime clockOut = entry.getClockOut();
      DateTime key = bucketer.getKey(entry.getClockIn());
      while (key.isBefore(clockOut) && key.isBefore(reportInterval.getEnd())) {
        bucketer.add(key, entry.getPayRate());
        key = key.plusHours(1);
      }
    }
    return bucketer;
  }

  static class Report {
    @Key
    private final String report;
    @Key
    private final String timeInterval;
    @Key
    private final List<Data> data;

    Report(
        @JsonProperty("report") String report,
        @JsonProperty("timeInterval") String timeInterval,
        @JsonProperty("data") List<Data> data) {
      this.report = report;
      this.timeInterval = timeInterval;
      this.data = data;
    }

    String getReport() {
      return report;
    }

    String getTimeInterval() {
      return timeInterval;
    }

    List<Data> getData() {
      return data;
    }
  }

  static class Data {
    @Key
    private final TimeFrame timeFrame;
    @Key
    private final BigDecimal value;

    Data(@JsonProperty("timeFrame") TimeFrame timeFrame, @JsonProperty("value") BigDecimal value) {
      this.timeFrame = timeFrame;
      this.value = value;
    }

    TimeFrame getTimeFrame() {
      return timeFrame;
    }

    BigDecimal getValue() {
      return value;
    }
  }

  static class TimeFrame {
    @Key
    private final String start;
    @Key
    private final String end;

    TimeFrame(@JsonProperty("start") String start, @JsonProperty("end") String end) {
      this.start = start;
      this.end = end;
    }

    String getStart() {
      return start;
    }

    String getEnd() {
      return end;
    }
  }
}
