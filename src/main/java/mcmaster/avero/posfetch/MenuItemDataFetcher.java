package mcmaster.avero.posfetch;

import com.google.common.annotations.VisibleForTesting;
import mcmaster.avero.posfetch.data.MenuItem;
import mcmaster.avero.posfetch.data.MenuItems;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

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
