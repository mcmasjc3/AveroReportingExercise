package mcmaster.avero.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Domain class representing a batch of ordered items fetched from the POS system. Created from JSON
 * in production.
 */
public class OrderedItems {
  private final int count;
  private final List<OrderedItem> orderedItems;

  @JsonCreator
  public OrderedItems(
      @JsonProperty("count") int count, @JsonProperty("data") List<OrderedItem> orderedItems) {
    this.count = count;
    this.orderedItems = orderedItems;
  }

  public int getCount() {
    return count;
  }

  public List<OrderedItem> getOrderedItems() {
    return orderedItems;
  }
}
