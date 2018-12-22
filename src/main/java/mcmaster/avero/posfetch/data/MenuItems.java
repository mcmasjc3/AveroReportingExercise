package mcmaster.avero.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MenuItems {
  private final int count;
  private final List<MenuItem> menuItems;

  @JsonCreator
  public MenuItems(
      @JsonProperty("count") int count, @JsonProperty("data") List<MenuItem> menuItems) {
    this.count = count;
    this.menuItems = menuItems;
  }

  public int getCount() {
    return count;
  }

  public List<MenuItem> getMenuItems() {
    return menuItems;
  }
}
