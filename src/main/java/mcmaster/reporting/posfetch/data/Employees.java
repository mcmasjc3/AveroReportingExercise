package mcmaster.reporting.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Domain class representing a batch of employees fetched from the POS system. Created from JSON in
 * production.
 */
public class Employees {
  private final int count;
  private final List<Employee> employees;

  @JsonCreator
  public Employees(
      @JsonProperty("count") int count, @JsonProperty("data") List<Employee> employees) {
    this.count = count;
    this.employees = employees;
  }

  public int getCount() {
    return count;
  }

  public List<Employee> getEmployees() {
    return employees;
  }
}
