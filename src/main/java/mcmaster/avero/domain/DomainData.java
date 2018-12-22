package mcmaster.avero.domain;

import mcmaster.avero.posfetch.data.Business;
import mcmaster.avero.posfetch.data.Check;
import mcmaster.avero.posfetch.data.Employee;
import mcmaster.avero.posfetch.data.LaborEntry;
import mcmaster.avero.posfetch.data.MenuItem;
import mcmaster.avero.posfetch.data.OrderedItem;
import org.joda.time.Interval;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class DomainData {

  private Map<String, Business> businesses;
  private Map<String, Check> checks;
  private Map<String, Employee> employees;
  private Map<String, LaborEntry> laborEntries;
  private Map<String, MenuItem> menuItems;
  private Map<String, OrderedItem> orderedItems;

  @Inject
  public DomainData(
      Map<String, Business> businesses,
      Map<String, Check> checks,
      Map<String, Employee> employees,
      Map<String, LaborEntry> laborEntries,
      Map<String, MenuItem> menuItems,
      Map<String, OrderedItem> orderedItems) {
    this.businesses = businesses;
    this.checks = checks;
    this.employees = employees;
    this.laborEntries = laborEntries;
    this.menuItems = menuItems;
    this.orderedItems = orderedItems;
  }

  public Optional<Business> getBusiness(String businessId) {
    return Optional.ofNullable(businesses.get(businessId));
  }

  public List<OrderedItem> getOrderedItems(String businessId, Interval reportInterval) {
    return orderedItems
        .values()
        .stream()
        .filter((o) -> !o.isVoided())
        .filter((o) -> o.getBusinessId().equals(businessId))
        .filter((o) -> reportInterval.contains(o.getCreatedAt()))
        .collect(Collectors.toList());
  }

  public List<LaborEntry> getLaborEntries(String businessId, Interval reportInterval) {
    return laborEntries
        .values()
        .stream()
        .filter((o) -> o.getBusinessId().equals(businessId))
        .filter((o) -> reportInterval.contains(o.getClockIn()) || reportInterval.contains(o.getClockOut()))
        .collect(Collectors.toList());
  }
}
