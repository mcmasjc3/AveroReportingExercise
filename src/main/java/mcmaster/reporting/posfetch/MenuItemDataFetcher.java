package mcmaster.reporting.posfetch;

import com.google.common.annotations.VisibleForTesting;
import mcmaster.reporting.posfetch.data.MenuItem;
import mcmaster.reporting.posfetch.data.MenuItems;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Data fetcher that translates JSON into {@link MenuItem} domain classes.
 */
public class MenuItemDataFetcher extends AbstractDataFetcher<MenuItem> {
  private static final String REQUEST = "/menuItems";

  @Inject
  @VisibleForTesting
  MenuItemDataFetcher(RequestHandler requestHandler) {
    super(requestHandler, REQUEST);
  }

  @Override
  List<MenuItem> translateData(String datum) throws IOException {
    return OBJECT_MAPPER.readValue(datum, MenuItems.class).getMenuItems();
  }
}
