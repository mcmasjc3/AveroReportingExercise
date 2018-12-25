package mcmaster.reporting.posfetch;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import mcmaster.reporting.posfetch.data.Check;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

/**
 * Tests for {@link CheckDataFetcher}
 */
public class CheckDataFetcherTest {
  @Test
  public void testSendRequest() {
    int count = 1;
    String id = "1234";
    String businessId = "2345";
    String employeeId = "3456";
    String name = "test1";
    boolean closed = true;
    String closedAt = "2018-12-20T23:45:01.890Z";
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
            + "\",\"name\":\""
            + name
            + "\",\"closed\":"
            + closed
            + ",\"closed_at\":\""
            + closedAt
            + "\",\"updated_at\":\""
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
    CheckDataFetcher handler = new CheckDataFetcher(requestHandler);
    Map<String, Check> checks = handler.getData();
    assertThat(checks.size()).isEqualTo(1);
    Check check = checks.get(id);
    assertThat(check).isNotNull();
    assertThat(check.getId()).isEqualTo(id);
    assertThat(check.getBusinessId()).isEqualTo(businessId);
    assertThat(check.getEmployeeId()).isEqualTo(employeeId);
    assertThat(check.getName()).isEqualTo(name);
    assertThat(check.isClosed()).isEqualTo(closed);
    assertThat(check.getClosedAt()).isEqualTo(DateTime.parse(closedAt));
    assertThat(check.getUpdatedAt()).isEqualTo(DateTime.parse(updated));
    assertThat(check.getCreatedAt()).isEqualTo(DateTime.parse(created));
  }
}
