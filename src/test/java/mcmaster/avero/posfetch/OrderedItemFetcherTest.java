package mcmaster.avero.posfetch;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import mcmaster.avero.posfetch.data.OrderedItem;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class OrderedItemFetcherTest {
  @Test
  public void testSendRequest() {
    int count = 1;
    String id = "1234";
    String businessId = "2345";
    String employeeId = "3456";
    String checkId = "4567";
    String itemId = "5678";
    String name = "test1";
    int cost = 17;
    int price = 19;
    boolean voided = true;
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
            + "\",\"check_id\":\""
            + checkId
            + "\",\"item_id\":\""
            + itemId
            + "\",\"name\":\""
            + name
            + "\",\"cost\":"
            + cost
            + ",\"price\":"
            + price
            + ",\"voided\":"
            + voided
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
    OrderedItemDataFetcher handler = new OrderedItemDataFetcher(requestHandler);
    Map<String, OrderedItem> orderedItems = handler.getData();
    assertThat(orderedItems.size()).isEqualTo(1);
    OrderedItem orderedItem = orderedItems.get(id);
    assertThat(orderedItem).isNotNull();
    assertThat(orderedItem.getId()).isEqualTo(id);
    assertThat(orderedItem.getBusinessId()).isEqualTo(businessId);
    assertThat(orderedItem.getEmployeeId()).isEqualTo(employeeId);
    assertThat(orderedItem.getCheckId()).isEqualTo(checkId);
    assertThat(orderedItem.getItemId()).isEqualTo(itemId);
    assertThat(orderedItem.getName()).isEqualTo(name);
    assertThat(orderedItem.getCost()).isEqualTo(cost);
    assertThat(orderedItem.getPrice()).isEqualTo(price);
    assertThat(orderedItem.isVoided()).isEqualTo(voided);
    assertThat(orderedItem.getUpdatedAt()).isEqualTo(DateTime.parse(updated));
    assertThat(orderedItem.getCreatedAt()).isEqualTo(DateTime.parse(created));
  }
}
