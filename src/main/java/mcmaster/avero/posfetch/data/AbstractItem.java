package mcmaster.avero.posfetch.data;

/**
 * Superclass for all domain objects that represent items fetched from the POS system.
 */
public abstract class AbstractItem {
  private final String id;

  AbstractItem(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
