package mcmaster.avero.posdata;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import mcmaster.avero.data.Business;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class BusinessDataFetcherTest {
  @Test
  public void testSendRequest() throws Exception {
    int count = 1;
    String id = "1234";
    String name = "test1";
    String hours = "[1,2,3]";
    String updatedAt = "2018-12-19T01:23:45.678Z";
    String createdAt = "2018-12-18T12:34:56.789Z";
    String content =
        "{\"count\":"
            + count
            + ",\"data\":[{\"id\":\""
            + id
            + "\",\"name\":\""
            + name
            + "\",\"hours\":"
            + hours
            + ",\"updated_at\":\""
            + updatedAt
            + "\",\"created_at\":\""
            + createdAt
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
    BusinessDataFetcher fetcher = new BusinessDataFetcher(requestHandler);
    Map<String, Business> businesses = fetcher.getData();
    assertThat(businesses.size()).isEqualTo(1);
    Business business = businesses.get(id);
    assertThat(business).isNotNull();
    assertThat(business.getId()).isEqualTo(id);
    assertThat(business.getName()).isEqualTo(name);
    assertThat(business.getHours()).containsExactly(1, 2, 3);
    assertThat(business.getUpdatedAt()).isEqualTo(DateTime.parse(updatedAt));
    assertThat(business.getCreatedAt()).isEqualTo(DateTime.parse(createdAt));
  }
}
