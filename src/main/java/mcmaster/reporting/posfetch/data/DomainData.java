package mcmaster.reporting.posfetch.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.joda.time.Interval;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class that holds all the data from the POS system.
 *
 * <p>Marked as a Singleton so Guice creates only one copy per run.
 */
public class DomainData {

  private final Map<String, Business> businesses;
  private final Map<String, Check> checks;
  private final Map<String, Employee> employees;
  private final Map<String, LaborEntry> laborEntries;
  private final Map<String, MenuItem> menuItems;
  private final Map<String, OrderedItem> orderedItems;

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

  /**
   * Returns a single {@link Business} specified by ID.
   *
   * <p>Uses {@link Optional} so the caller doesn't have to do a null check.
   */
  public Optional<Business> getBusiness(String businessId) {
    return Optional.ofNullable(businesses.get(businessId));
  }

  /**
   * Returns all {@link OrderedItem OrderedItems} for a {@link Business} that were created during
   * the given {@link Interval} and not voided.
   */
  public List<OrderedItem> getOrderedItems(String businessId, Interval reportInterval) {
    return ImmutableList.copyOf(
        orderedItems
            .values()
            .stream()
            .filter((o) -> !o.isVoided())
            .filter((o) -> o.getBusinessId().equals(businessId))
            .filter((o) -> reportInterval.contains(o.getCreatedAt()))
            .collect(Collectors.toList()));
  }

  /**
   * Returns all {@link LaborEntry LaborEntries} for a {@link Business} that were created during the
   * given {@link Interval} and not voided.
   */
  public List<LaborEntry> getLaborEntries(String businessId, Interval reportInterval) {
    return ImmutableList.copyOf(
        laborEntries
            .values()
            .stream()
            .filter((o) -> o.getBusinessId().equals(businessId))
            .filter(
                (o) ->
                    reportInterval.contains(o.getClockIn())
                        || reportInterval.contains(o.getClockOut()))
            .collect(Collectors.toList()));
  }

  /**
   * Returns a {@link Map} of all employee names for a {@link Business}, indexed by employee id.
   */
  public Map<String, String> getEmployeeNames(String businessId) {
    return ImmutableMap.copyOf(
        this.employees
            .values()
            .stream()
            .filter((o) -> o.getBusinessId().equals(businessId))
            .collect(
                Collectors.toMap(
                    Employee::getId,
                    (e) -> String.format("%s %s", e.getFirstName(), e.getLastName()))));
  }
}
