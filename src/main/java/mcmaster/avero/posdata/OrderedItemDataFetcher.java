package mcmaster.avero.posdata;

import mcmaster.avero.data.OrderedItem;
import mcmaster.avero.data.OrderedItems;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

class OrderedItemDataFetcher extends GenericDataFetcher<OrderedItem> {
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
