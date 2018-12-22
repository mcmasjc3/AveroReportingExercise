package mcmaster.avero.reports;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import mcmaster.avero.domain.DomainData;
import mcmaster.avero.posfetch.data.Business;
import mcmaster.avero.posfetch.data.OrderedItem;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class FoodCostPercentageReportTest {

  private static final String BUSINESS_ID = "business1";
  private static final String ORDERED_ITEM_1 = "order1";
  private static final Map<String, Business> BUSINESSES =
      ImmutableMap.of(
          BUSINESS_ID, new Business(BUSINESS_ID, "", ImmutableList.of(), "2018", "2018"));
  private static final Map<String, OrderedItem> ORDERED_ITEMS =
      ImmutableMap.of(
          ORDERED_ITEM_1,
          new OrderedItem(
              ORDERED_ITEM_1,
              BUSINESS_ID,
              null,
              null,
              null,
              null,
              10,
              17,
              false,
              "2018-12-20T12:34:00.000Z",
              "2018-12-20T12:34:00.000Z"));

  @Test
  public void testGenerateReport_invalidBusiness() {
    DomainData domainData = new DomainData(BUSINESSES, null, null, null, null, null);
    LaborCostPercentageReport report = new LaborCostPercentageReport(domainData);
    assertThrows(InvalidReportException.class, () -> report.generate("No business", null, null));
  }

  @Test
  public void testGenerateHourReport() throws Exception {
    DomainData domainData =
        new DomainData(BUSINESSES, null, null, null, null, ORDERED_ITEMS);
    DateTime start = new DateTime(2018, 12, 20, 8, 30, 0, 0, DateTimeZone.UTC);
    DateTime end = new DateTime(2018, 12, 20, 17, 0, 0, 0, DateTimeZone.UTC);
    Interval reportInterval = new Interval(start, end);
    FoodCostPercentageReport generator = new FoodCostPercentageReport(domainData);
    String result = generator.generate(BUSINESS_ID, reportInterval, BucketerType.HOUR);
    FoodCostPercentageReport.Report report =
        FoodCostPercentageReport.OBJECT_MAPPER.readValue(
            result, FoodCostPercentageReport.Report.class);
    assertThat(report.getReport()).isEqualTo(FoodCostPercentageReport.REPORT_TYPE);
    assertThat(report.getTimeInterval()).isEqualTo(BucketerType.HOUR.toString());
    List<FoodCostPercentageReport.Data> data = report.getData();
    assertThat(data.size()).isEqualTo(1);
    FoodCostPercentageReport.Data datum1 = data.get(0);
    FoodCostPercentageReport.TimeFrame timeFrame1 = datum1.getTimeFrame();
    assertThat(timeFrame1.getStart())
        .isEqualTo(new DateTime(2018, 12, 20, 12, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(timeFrame1.getEnd())
        .isEqualTo(new DateTime(2018, 12, 20, 13, 0, 0, 0, DateTimeZone.UTC).toString());
    assertThat(datum1.getValue().toString()).isEqualTo("58.8");
  }
}
