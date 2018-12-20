package mcmaster.avero.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class MenuItem implements GenericItem {
  private final String id;
  private final String businessId;
  private final String name;
  private final int cost;
  private final int price;
  private final DateTime updatedAt;
  private final DateTime createdAt;

  @JsonCreator
  public MenuItem(
      @JsonProperty("id") String id,
      @JsonProperty("business_id") String businessId,
      @JsonProperty("name") String name,
      @JsonProperty("cost") int cost,
      @JsonProperty("price") int price,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("created_at") String createdAt) {
    this.id = id;
    this.businessId = businessId;
    this.name = name;
    this.cost = cost;
    this.price = price;
    this.updatedAt = DateTime.parse(updatedAt);
    this.createdAt = DateTime.parse(createdAt);
  }

  public String getId() {
    return id;
  }

  public String getBusinessId() {
    return businessId;
  }

  public String getName() {
    return name;
  }

  public int getCost() {
    return cost;
  }

  public int getPrice() {
    return price;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }
}
