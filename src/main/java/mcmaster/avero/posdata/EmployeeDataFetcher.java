package mcmaster.avero.posdata;

import mcmaster.avero.data.Employee;
import mcmaster.avero.data.Employees;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

class EmployeeDataFetcher extends GenericDataFetcher<Employee> {
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
