package mcmaster.reporting.posfetch;

import mcmaster.reporting.posfetch.data.OrderedItem;
import mcmaster.reporting.posfetch.data.OrderedItems;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Data fetcher that translates JSON into {@link OrderedItem} domain classes.
 */
public class OrderedItemDataFetcher extends AbstractDataFetcher<OrderedItem> {
  private static final String REQUEST = "/orderedItems";

  @Inject
  OrderedItemDataFetcher(RequestHandler requestHandler) {
    super(requestHandler, REQUEST);
  }

  @Override
  List<OrderedItem> translateData(String datum) throws IOException {
    return OBJECT_MAPPER.readValue(datum, OrderedItems.class).getOrderedItems();
  }
}
