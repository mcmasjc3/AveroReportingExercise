package mcmaster.avero.domain;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import mcmaster.avero.guice.AveroModule;
import mcmaster.avero.posfetch.data.Business;
import mcmaster.avero.posfetch.data.OrderedItem;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class DomainDataTest {

  private static final String BUSINESS_ID1 = "business1";
  private static final String BUSINESS_ID2 = "business2";
  private static final Business BUSINESS1 = new Business(BUSINESS_ID1, null, null, "2018", "2018");
  private static final Business BUSINESS2 = new Business(BUSINESS_ID2, null, null, "2018", "2018");
  private static final ImmutableMap<String, Business> BUSINESSES =
      ImmutableMap.of(BUSINESS_ID1, BUSINESS1, BUSINESS_ID2, BUSINESS2);

  //  @Test
  public void testDataFetch() {
    Injector injector = Guice.createInjector(new AveroModule());
    DomainData domainData = injector.getInstance(DomainData.class);
    assertThat(domainData).isNotNull();
  }

  @Test
  public void testGetValidBusiness() {
    DomainData data = new DomainData(BUSINESSES, null, null, null, null, null);
    assertThat(data.getBusiness(BUSINESS_ID1).isPresent()).isTrue();
  }

  @Test
  public void testGetInvalidBusiness() {
    DomainData data = new DomainData(BUSINESSES, null, null, null, null, null);
    assertThat(data.getBusiness("not a business").isPresent()).isFalse();
  }

  @Test
  public void testGetOrderedItems() {
    Interval reportInterval =
        new Interval(
            new DateTime(2018, 12, 20, 8, 0, 0, 0, DateTimeZone.UTC),
            new DateTime(2018, 12, 20, 17, 0, 0, 0, DateTimeZone.UTC));
    OrderedItem goodItem1 =
        new OrderedItem(
            "1",
            BUSINESS_ID1,
            null,
            null,
            null,
            null,
            0,
            0,
            false,
            "2018-12-20T12:34:00.000Z",
            "2018-12-20T12:34:00.000Z");
    OrderedItem goodItem2WithUpdatedOutsideRange =
        new OrderedItem(
            "2",
            BUSINESS_ID1,
            null,
            null,
            null,
            null,
            0,
            0,
            false,
            "2018-12-20T18:00:00.000Z",
            "2018-12-20T16:00:00.000Z");
    OrderedItem itemOutsideRange =
        new OrderedItem(
            "3",
            BUSINESS_ID1,
            null,
            null,
            null,
            null,
            0,
            0,
            false,
            "2018-12-19T12:34:00.000Z",
            "2018-12-19T12:34:00.000Z");
    OrderedItem voidedItem =
        new OrderedItem(
            "4",
            BUSINESS_ID1,
            null,
            null,
            null,
            null,
            0,
            0,
            true,
            "2018-12-20T12:34:00.000Z",
            "2018-12-19T12:34:00.000Z");
    OrderedItem otherBusinessItem =
        new OrderedItem(
            "5",
            BUSINESS_ID2,
            null,
            null,
            null,
            null,
            0,
            0,
            false,
            "2018-12-20T12:34:00.000Z",
            "2018-12-20T12:34:00.000Z");
    Map<String, OrderedItem> orderedItems =
        ImmutableMap.of(
            "1",
            goodItem1,
            "2",
            goodItem2WithUpdatedOutsideRange,
            "3",
            itemOutsideRange,
            "4",
            voidedItem,
            "5",
            otherBusinessItem);
    DomainData domainData = new DomainData(BUSINESSES, null, null, null, null, orderedItems);
    List<OrderedItem> result = domainData.getOrderedItems(BUSINESS_ID1, reportInterval);
    assertThat(result).containsExactly(goodItem1, goodItem2WithUpdatedOutsideRange);
  }
}
