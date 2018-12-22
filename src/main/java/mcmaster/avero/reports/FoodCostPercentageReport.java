package mcmaster.avero.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import mcmaster.avero.domain.DomainData;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

class FoodCostPercentageReport
    extends AbstractReport<FoodCostPercentageReport.Report, FoodCostPercentageReport.Data> {
  static final String REPORT_TYPE = "FCP";

  @Inject
  FoodCostPercentageReport(DomainData domainData) {
    super(domainData, REPORT_TYPE);
  }

  @Override
  Report createReport(String businessId, String timeFrame, List<Data> data) {
    return new Report(businessId, timeFrame, data);
  }

  List<Data> generateData(String businessId, Interval reportInterval, BucketerType bucketerType) {
    ImmutableList.Builder<Data> result = ImmutableList.builder();
    ImmutableMap<DateTime, Bucketer.Bucket<Integer>> costs =
        bucketCosts(businessId, reportInterval, bucketerType).getBuckets();
    Bucketer<Integer> sales = bucketSales(businessId, reportInterval, bucketerType);
    for (Bucketer.Bucket<Integer> salesBucket : sales.getBuckets().values()) {
      BigDecimal totalSales =
          new BigDecimal(salesBucket.values.stream().collect((Collectors.summingInt((s) -> s))));
      BigDecimal totalCosts =
          new BigDecimal(
              costs
                  .get(salesBucket.start)
                  .values
                  .stream()
                  .collect((Collectors.summingInt((s) -> s))));
      result.add(
          new Data(
              new TimeFrame(salesBucket.start.toString(), salesBucket.end.toString()),
              totalCosts
                  .divide(totalSales, 3, BigDecimal.ROUND_HALF_UP)
                  .multiply(new BigDecimal(100))
                  .setScale(1, BigDecimal.ROUND_HALF_UP)));
    }
    return result.build();
  }

  private Bucketer<Integer> bucketSales(
      String businessId, Interval reportInterval, BucketerType bucketerType) {
    Bucketer<Integer> bucketer = new Bucketer<>(bucketerType);
    domainData
        .getOrderedItems(businessId, reportInterval)
        .forEach((o) -> bucketer.add(o.getCreatedAt(), o.getPrice()));
    return bucketer;
  }

  private Bucketer<Integer> bucketCosts(
      String businessId, Interval reportInterval, BucketerType bucketerType) {
    Bucketer<Integer> bucketer = new Bucketer<>(bucketerType);
    domainData
        .getOrderedItems(businessId, reportInterval)
        .forEach((o) -> bucketer.add(o.getCreatedAt(), o.getCost()));
    return bucketer;
  }

  static class Report {
    @Key
    private String report;
    @Key
    private String timeInterval;
    @Key
    private List<Data> data;

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
    private TimeFrame timeFrame;
    @Key
    private BigDecimal value;

    Data(
        @JsonProperty("timeFrame") TimeFrame timeFrame, @JsonProperty("value") BigDecimal value) {
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
    private String start;
    @Key
    private String end;

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
