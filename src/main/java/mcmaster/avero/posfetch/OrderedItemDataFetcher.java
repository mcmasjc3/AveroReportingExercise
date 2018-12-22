package mcmaster.avero.posfetch;

import mcmaster.avero.posfetch.data.OrderedItem;
import mcmaster.avero.posfetch.data.OrderedItems;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

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
