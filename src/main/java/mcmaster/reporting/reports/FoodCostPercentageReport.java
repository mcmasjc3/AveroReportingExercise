package mcmaster.reporting.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import mcmaster.reporting.posfetch.data.Business;
import mcmaster.reporting.posfetch.data.DomainData;
import mcmaster.reporting.posfetch.data.OrderedItem;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates a food cost percentage report.
 */
public class FoodCostPercentageReport
    extends ReportGenerator<
    FoodCostPercentageReport.Report, FoodCostPercentageReport.Data> {
  static final ReportType REPORT_TYPE = ReportType.FCP;

  @Inject
  FoodCostPercentageReport(DomainData domainData) {
    super(domainData, REPORT_TYPE);
  }

  /**
   * Returns the report for the percentage of food costs in total sales for the requested {@link
   * Business} and {@link Interval}.
   */
  @Override
  Report createReport(ReportType reportType, String timeFrame, List<Data> data) {
    return new Report(reportType.name(), timeFrame, data);
  }

  /**
   * Returns Data items for the report.
   *
   * <p>We create {@link Bucketer Bucketers} for costs and prices for all {@link OrderedItem
   * OrderedItems} for the given {@link Business} and {@link Interval}.
   *
   * <p>Once we have those, we run through the {@link Bucketer.Bucket Buckets} and do the division,
   * creating a {@link Data} for each bucket.
   */
  List<Data> generateData(String businessId, Interval reportInterval, BucketerType bucketerType) {
    ImmutableList.Builder<Data> result = ImmutableList.builder();
    ImmutableMap<DateTime, Bucketer.Bucket<Integer>> costs =
        bucketCosts(businessId, reportInterval, bucketerType).getBuckets();
    Bucketer<Integer> sales = bucketSales(businessId, reportInterval, bucketerType);
    for (Bucketer.Bucket<Integer> salesBucket : sales.getBuckets().values()) {
      BigDecimal totalSales =
          new BigDecimal(
              salesBucket.getValues().stream().collect((Collectors.summingInt((s) -> s))));
      BigDecimal totalCosts =
          new BigDecimal(
              costs
                  .get(salesBucket.getStart())
                  .getValues()
                  .stream()
                  .collect((Collectors.summingInt((s) -> s))));
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
        .forEach((o) -> bucketer.add(o.getCreatedAt(), o.getPrice()));
    return bucketer;
  }

  /**
   * Create a {@link Bucketer} for the given {@link BucketerType} and add the cost for each {@link
   * OrderedItem}.
   */
  private Bucketer<Integer> bucketCosts(
      String businessId, Interval reportInterval, BucketerType bucketerType) {
    Bucketer<Integer> bucketer = Bucketer.create(bucketerType);
    domainData
        .getOrderedItems(businessId, reportInterval)
        .forEach((o) -> bucketer.add(o.getCreatedAt(), o.getCost()));
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
