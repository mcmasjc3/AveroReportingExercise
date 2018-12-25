package mcmaster.reporting.posfetch;

import mcmaster.reporting.posfetch.data.Employee;
import mcmaster.reporting.posfetch.data.Employees;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Data fetcher that translates JSON into {@link Employee} domain classes.
 */
public class EmployeeDataFetcher extends AbstractDataFetcher<Employee> {
  private static final String REQUEST = "/employees";

  @Inject
  EmployeeDataFetcher(RequestHandler requestHandler) {
    super(requestHandler, REQUEST);
  }

  @Override
  List<Employee> translateData(String datum) throws IOException {
    return OBJECT_MAPPER.readValue(datum, Employees.class).getEmployees();
  }
}
