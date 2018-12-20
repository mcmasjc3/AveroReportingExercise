package mcmaster.avero.posdata;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import mcmaster.avero.data.Employee;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class EmployeeDataFetcherTest {
  @Test
  public void testSendRequest() throws Exception {
    int count = 1;
    String id = "1234";
    String businessId = "2345";
    String employeeId = "3456";
    String firstName = "first";
    String lastName = "last";
    int payRate = 17;
    String updated = "2018-12-19T01:23:45.678Z";
    String created = "2018-12-18T12:34:56.789Z";
    String content =
        "{\"count\":"
            + count
            + ",\"data\":[{\"id\":\""
            + id
            + "\",\"business_id\":\""
            + businessId
            + "\",\"employee_id\":\""
            + employeeId
            + "\",\"first_name\":\""
            + firstName
            + "\",\"last_name\":\""
            + lastName
            + "\",\"pay_rate\":"
            + payRate
            + ",\"updated_at\":\""
            + updated
            + "\",\"created_at\":\""
            + created
            + "\"}]}";
    RequestHandler requestHandler =
        new RequestHandler(
            new MockHttpTransport() {
              @Override
              public LowLevelHttpRequest buildRequest(String method, String url) {
                return new MockLowLevelHttpRequest() {
                  @Override
                  public LowLevelHttpResponse execute() {
                    MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                    response.setStatusCode(200);
                    response.setContentType(Json.MEDIA_TYPE);
                    response.setContent(content);
                    return response;
                  }
                };
              }
            });
    EmployeeDataFetcher handler = new EmployeeDataFetcher(requestHandler);
    Map<String, Employee> employees = handler.getData();
    assertThat(employees.size()).isEqualTo(1);
    Employee employee = employees.get(id);
    assertThat(employee).isNotNull();
    assertThat(employee.getId()).isEqualTo(id);
    assertThat(employee.getBusinessId()).isEqualTo(businessId);
    assertThat(employee.getEmployeeId()).isEqualTo(employeeId);
    assertThat(employee.getFirstName()).isEqualTo(firstName);
    assertThat(employee.getLastName()).isEqualTo(lastName);
    assertThat(employee.getPayRate()).isEqualTo(payRate);
    assertThat(employee.getUpdatedAt()).isEqualTo(DateTime.parse(updated));
    assertThat(employee.getCreatedAt()).isEqualTo(DateTime.parse(created));
  }
}