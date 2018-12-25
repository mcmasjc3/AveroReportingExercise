package mcmaster.reporting.reports;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import mcmaster.reporting.posfetch.data.Business;
import mcmaster.reporting.posfetch.data.DomainData;
import mcmaster.reporting.posfetch.data.Employee;
import mcmaster.reporting.posfetch.data.OrderedItem;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * Tests for {@link EmployeeGrossSalesReport}
 */
public class EmployeeGrossSalesReportTest {

  private static final String BUSINESS_ID = "business1";
  private static final String ORDERED_ITEM_1 = "order1";
  private static final String ORDERED_ITEM_2 = "order2";
  private static final String ORDERED_ITEM_3 = "order3";
  private static final String ORDERED_ITEM_4 = "order4";
  private static final String ORDERED_ITEM_5 = "order5";
  private static final String EMPLOYEE_1 = "employee1";
  private static final String EMPLOYEE_FIRSTNAME_1 = "Moses";
  private static final String EMPLOYEE_LASTNAME_1 = "Horwitz";
  private static final String EMPLOYEE_2 = "employee2";
  private static final String EMPLOYEE_FIRSTNAME_2 = "Louis";
  private static final String EMPLOYEE_LASTNAME_2 = "Feinberg";
  private static final Map<String, Business> BUSINESSES =
      ImmutableMap.of(
          BUSINESS_ID, new Business(BUSINESS_ID, "", ImmutableList.of(), "2018", "2018"));
  private static final Map<String, OrderedItem> ORDERED_ITEMS =
      ImmutableMap.of(
          ORDERED_ITEM_1,
          new OrderedItem(
              ORDERED_ITEM_1,
              BUSINESS_ID,
              EMPLOYEE_1,
              null,
              null,
              null,
              0,
              17,
              false,
              "2018-12-20T10:34:00.000Z",
              "2018-12-20T10:34:00.000Z"),
          ORDERED_ITEM_2,
          new OrderedItem(
              ORDERED_ITEM_2,
              BUSINESS_ID,
              EMPLOYEE_1,
              null,
              null,
              null,
              0,
              19,
              false,
              "2018-12-20T13:34:00.000Z",
              "2018-12-20T13:34:00.000Z"),
          ORDERED_ITEM_3,
          new OrderedItem(
              ORDERED_ITEM_3,
              BUSINESS_ID,
              EMPLOYEE_2,
              null,
              null,
              null,
              0,
              23,
              false,
              "2018-12-20T13:34:00.000Z",
              "2018-12-20T13:34:00.000Z"),
          ORDERED_ITEM_4,
          new OrderedItem(
              ORDERED_ITEM_4,
              BUSINESS_ID,
              EMPLOYEE_2,
              null,
              null,
              null,
              0,
              29,
              false,
              "2018-12-20T15:34:00.000Z",
              "2018-12-20T15:34:00.000Z"),
          ORDERED_ITEM_5,
          new OrderedItem(
              ORDERED_ITEM_5,
              BUSINESS_ID,
              EMPLOYEE_2,
              null,
              null,
              null,
              0,
              31,
              false,
              "2018-12-20T15:34:00.000Z",
              "2018-12-20T15:34:00.000Z"));
  private static final Map<String, Employee> EMPLOYEES =
      ImmutableMap.of(
          EMPLOYEE_1,
          new Employee(
              EMPLOYEE_1,
              BUSINESS_ID,
              EMPLOYEE_FIRSTNAME_1,
              EMPLOYEE_LASTNAME_1,
              11,
              "2018",
              "2018"),
          EMPLOYEE_2,
          new Employee(
              EMPLOYEE_2,
              BUSINESS_ID,
              EMPLOYEE_FIRSTNAME_2,
              EMPLOYEE_LASTNAME_2,
              12,
              "2018",
              "2018"));

  @Test
  public void testGenerateReport_invalidBusiness() {
    DomainData domainData = new DomainData(BUSINESSES, null, null, null, null, null);
    EmployeeGrossSalesReport report = new EmployeeGrossSalesReport(domainData);
    assertThrows(InvalidReportException.class, () -> report.generate("No business", null, null));
  }

  @Test
  public void testGenerateHourReport() throws Exception {
    DomainData domainData = new DomainData(BUSINESSES, null, EMPLOYEES, null, null, ORDERED_ITEMS);
    DateTime start = new DateTime(2018, 12, 20, 8, 30, 0, 0, DateTimeZone.UTC);
    DateTime end = new DateTime(2018, 12, 20, 17, 0, 0, 0, DateTimeZone.UTC);
    Interval reportInterval = new Interval(start, end);
    EmployeeGrossSalesReport generator = new EmployeeGrossSalesReport(domainData);
    String result = generator.generate(BUSINESS_ID, reportInterval, BucketerType.HOUR);
    EmployeeGrossSalesReport.Report report =
        EmployeeGrossSalesReport.OBJECT_MAPPER.readValue(
            result, EmployeeGrossSalesReport.Report.class);
    assertThat(report.getReport()).isEqualTo(EmployeeGrossSalesReport.REPORT_TYPE.name());
    assertThat(report.getTimeInterval()).isEqualTo(BucketerType.HOUR.toString());
    List<EmployeeGrossSalesReport.Data> data = report.getData();
    assertThat(data.size()).isEqualTo(4);
    EmployeeGrossSalesReport.Data datum1 = data.get(0);
    EmployeeGrossSalesReport.TimeFrame timeFrame1 = datum1.getTimeFrame();
    assertThat(timeFrame1.getStart())
        .isEqualTo(new DateTime(2018, 12, 20, 10, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(timeFrame1.getEnd())
        .isEqualTo(new DateTime(2018, 12, 20, 11, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(datum1.getEmployee()).isEqualTo(EMPLOYEE_FIRSTNAME_1 + " " + EMPLOYEE_LASTNAME_1);
    assertThat(datum1.getValue().toString()).isEqualTo("17.00");
    EmployeeGrossSalesReport.Data datum2 = data.get(1);
    EmployeeGrossSalesReport.TimeFrame timeFrame2 = datum2.getTimeFrame();
    assertThat(timeFrame2.getStart())
        .isEqualTo(new DateTime(2018, 12, 20, 13, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(timeFrame2.getEnd())
        .isEqualTo(new DateTime(2018, 12, 20, 14, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(datum2.getEmployee()).isEqualTo(EMPLOYEE_FIRSTNAME_2 + " " + EMPLOYEE_LASTNAME_2);
    assertThat(datum2.getValue().toString()).isEqualTo("23.00");
    EmployeeGrossSalesReport.Data datum3 = data.get(2);
    EmployeeGrossSalesReport.TimeFrame timeFrame3 = datum3.getTimeFrame();
    assertThat(timeFrame3.getStart())
        .isEqualTo(new DateTime(2018, 12, 20, 13, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(timeFrame3.getEnd())
        .isEqualTo(new DateTime(2018, 12, 20, 14, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(datum3.getEmployee()).isEqualTo(EMPLOYEE_FIRSTNAME_1 + " " + EMPLOYEE_LASTNAME_1);
    assertThat(datum3.getValue().toString()).isEqualTo("19.00");
    EmployeeGrossSalesReport.Data datum4 = data.get(3);
    EmployeeGrossSalesReport.TimeFrame timeFrame4 = datum4.getTimeFrame();
    assertThat(timeFrame4.getStart())
        .isEqualTo(new DateTime(2018, 12, 20, 15, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(timeFrame4.getEnd())
        .isEqualTo(new DateTime(2018, 12, 20, 16, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(datum4.getEmployee()).isEqualTo(EMPLOYEE_FIRSTNAME_2 + " " + EMPLOYEE_LASTNAME_2);
    assertThat(datum4.getValue().toString()).isEqualTo("60.00");
  }
}
