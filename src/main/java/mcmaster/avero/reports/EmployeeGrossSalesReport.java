package mcmaster.avero.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;
import com.google.common.collect.ImmutableList;
import mcmaster.avero.domain.DomainData;
import mcmaster.avero.posfetch.data.Business;
import mcmaster.avero.posfetch.data.Employee;
import mcmaster.avero.posfetch.data.OrderedItem;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Generates an employee gross sales report.
 */
class EmployeeGrossSalesReport
    extends AbstractReportGenerator<
    EmployeeGrossSalesReport.Report, EmployeeGrossSalesReport.Data> {
  static final String REPORT_TYPE = "EGS";

  @Inject
  EmployeeGrossSalesReport(DomainData domainData) {
    super(domainData, REPORT_TYPE);
  }

  /**
   * Returns the report for the gross sales by each employee for the requested {@link Business} and
   * {@link Interval}.
   */
  @Override
  Report createReport(String businessId, String timeFrame, List<Data> data) {
    return new Report(businessId, timeFrame, data);
  }

  /**
   * Returns Data items for the report.
   *
   * <p>We create a {@link SortedMap} of {@link Bucketer Bucketers} indexed by employee name. Each
   * Bucketer contains the prices for all {@link OrderedItem OrderedItems} sold by that employee for
   * the given {@link Business} and {@link Interval}. Once we have those, we run through all the
   * start times for Bucketer intervals during the report period.
   *
   * <p>For each start time, we iterate through the employee/Bucketer Map and see if the Bucketer
   * contains an Bucket for that time. If so, we generate a Data entry for the report.
   */
  List<Data> generateData(String businessId, Interval reportInterval, BucketerType bucketerType) {
    ImmutableList.Builder<Data> result = ImmutableList.builder();
    domainData.getEmployeeNames(businessId);
    SortedMap<String, Bucketer<Integer>> employeeSales =
        bucketSales(businessId, reportInterval, bucketerType);
    Bucketer<Integer> referenceBucketer = Bucketer.create(bucketerType);
    DateTime key = referenceBucketer.getKey(reportInterval.getStart());
    while (key.isBefore(reportInterval.getEnd())) {
      for (Map.Entry<String, Bucketer<Integer>> entry : employeeSales.entrySet()) {
        Bucketer<Integer> bucketer = entry.getValue();
        Optional<Bucketer.Bucket<Integer>> bucketOptional = bucketer.getBucket(key);
        if (bucketOptional.isPresent()) {
          Bucketer.Bucket<Integer> bucket = bucketOptional.get();
          result.add(
              new Data(
                  new TimeFrame(bucket.getStart().toString(), bucket.getEnd().toString()),
                  entry.getKey(),
                  new BigDecimal(bucket.getValues().stream().mapToInt((s) -> s).sum())
                      .setScale(2, BigDecimal.ROUND_UNNECESSARY)));
        }
      }
      key = referenceBucketer.getEnd(key);
    }

    return result.build();
  }

  /**
   * Create a {@link SortedMap} of {@link Bucketer Bucketers} indexed by employee name.
   *
   * <p>Each Bucketer contains all {@link OrderedItem OrderedItems} for an {@link Employee} for the
   * given {@link Business} during the given {@link Interval}.
   */
  private SortedMap<String, Bucketer<Integer>> bucketSales(
      String businessId, Interval reportInterval, BucketerType bucketerType) {
    Map<String, String> employeeNames = domainData.getEmployeeNames(businessId);
    SortedMap<String, Bucketer<Integer>> employeeSales = new TreeMap<>();
    for (OrderedItem orderedItem : domainData.getOrderedItems(businessId, reportInterval)) {
      String employeeName = employeeNames.get(orderedItem.getEmployeeId());
      employeeSales.putIfAbsent(employeeName, Bucketer.create(bucketerType));
      Bucketer<Integer> bucketer = employeeSales.get(employeeName);
      bucketer.add(orderedItem.getCreatedAt(), orderedItem.getPrice());
    }
    return employeeSales;
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
    private final String employee;
    @Key
    private final BigDecimal value;

    Data(
        @JsonProperty("timeFrame") TimeFrame timeFrame,
        @JsonProperty("employee") String employee,
        @JsonProperty("value") BigDecimal value) {
      this.timeFrame = timeFrame;
      this.employee = employee;
      this.value = value;
    }

    TimeFrame getTimeFrame() {
      return timeFrame;
    }

    public String getEmployee() {
      return employee;
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
