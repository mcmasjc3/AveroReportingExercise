package mcmaster.avero.posdata;

import com.google.common.annotations.VisibleForTesting;
import mcmaster.avero.data.MenuItem;
import mcmaster.avero.data.MenuItems;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

class MenuItemDataFetcher extends GenericDataFetcher<MenuItem> {
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
