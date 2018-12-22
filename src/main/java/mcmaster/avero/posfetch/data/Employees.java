package mcmaster.avero.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
