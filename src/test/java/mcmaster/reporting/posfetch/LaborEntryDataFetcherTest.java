package mcmaster.reporting.posfetch;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import mcmaster.reporting.posfetch.data.LaborEntry;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

/**
 * Tests for {@link LaborEntryDataFetcher}
 */
public class LaborEntryDataFetcherTest {
  @Test
  public void testSendRequest() {
    int count = 1;
    String id = "1234";
    String businessId = "2345";
    String employeeId = "3456";
    String name = "test1";
    String clockIn = "2018-12-19T09:00:00.000Z";
    String clockOut = "2018-12-19T17:00:00.000Z";
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
            + "\",\"name\":\""
            + name
            + "\",\"clock_in\":\""
            + clockIn
            + "\",\"clock_out\":\""
            + clockOut
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
    LaborEntryDataFetcher handler = new LaborEntryDataFetcher(requestHandler);
    Map<String, LaborEntry> laborEntries = handler.getData();
    assertThat(laborEntries.size()).isEqualTo(1);
    LaborEntry laborEntry = laborEntries.get(id);
    assertThat(laborEntry).isNotNull();
    assertThat(laborEntry.getId()).isEqualTo(id);
    assertThat(laborEntry.getBusinessId()).isEqualTo(businessId);
    assertThat(laborEntry.getEmployeeId()).isEqualTo(employeeId);
    assertThat(laborEntry.getName()).isEqualTo(name);
    assertThat(laborEntry.getClockIn()).isEqualTo(DateTime.parse(clockIn));
    assertThat(laborEntry.getClockOut()).isEqualTo(DateTime.parse(clockOut));
    assertThat(laborEntry.getPayRate()).isEqualTo(payRate);
    assertThat(laborEntry.getUpdatedAt()).isEqualTo(DateTime.parse(updated));
    assertThat(laborEntry.getCreatedAt()).isEqualTo(DateTime.parse(created));
  }
}
