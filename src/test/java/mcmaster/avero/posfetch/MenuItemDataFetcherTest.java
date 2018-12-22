package mcmaster.avero.posfetch;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import mcmaster.avero.posfetch.data.MenuItem;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class MenuItemDataFetcherTest {
  @Test
  public void testSendRequest() {
    int count = 1;
    String id = "1234";
    String businessId = "2345";
    String name = "test1";
    int cost = 17;
    int price = 19;
    String updatedAt = "2018-12-19T01:23:45.678Z";
    String createdAt = "2018-12-18T12:34:56.789Z";
    String content =
        "{\"count\":"
            + count
            + ",\"data\":[{\"id\":\""
            + id
            + "\",\"business_id\":\""
            + businessId
            + "\",\"name\":\""
            + name
            + "\",\"cost\":"
            + cost
            + ",\"price\":"
            + price
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
    MenuItemDataFetcher handler = new MenuItemDataFetcher(requestHandler);
    Map<String, MenuItem> menuItems = handler.getData();
    assertThat(menuItems.size()).isEqualTo(1);
    MenuItem menuItem = menuItems.get(id);
    assertThat(menuItem).isNotNull();
    assertThat(menuItem.getId()).isEqualTo(id);
    assertThat(menuItem.getBusinessId()).isEqualTo(businessId);
    assertThat(menuItem.getName()).isEqualTo(name);
    assertThat(menuItem.getCost()).isEqualTo(cost);
    assertThat(menuItem.getPrice()).isEqualTo(price);
    assertThat(menuItem.getUpdatedAt()).isEqualTo(DateTime.parse(updatedAt));
    assertThat(menuItem.getCreatedAt()).isEqualTo(DateTime.parse(createdAt));
  }
}
