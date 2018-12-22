package mcmaster.avero.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class OrderedItem implements GenericItem {
  private final String id;
  private final String businessId;
  private final String employeeId;
  private final String checkId;
  private final String itemId;
  private final String name;
  private final int cost;
  private final int price;
  private final boolean voided;
  private final DateTime updatedAt;
  private final DateTime createdAt;

  public String getId() {
    return id;
  }

  public String getBusinessId() {
    return businessId;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public String getCheckId() {
    return checkId;
  }

  public String getItemId() {
    return itemId;
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

  public boolean isVoided() {
    return voided;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }

  @JsonCreator
  public OrderedItem(
      @JsonProperty("id") String id,
      @JsonProperty("business_id") String businessId,
      @JsonProperty("employee_id") String employeeId,
      @JsonProperty("check_id") String checkId,
      @JsonProperty("item_id") String itemId,
      @JsonProperty("name") String name,
      @JsonProperty("cost") int cost,
      @JsonProperty("price") int price,
      @JsonProperty("voided") boolean voided,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("created_at") String createdAt) {
    this.id = id;
    this.businessId = businessId;
    this.employeeId = employeeId;
    this.checkId = checkId;
    this.itemId = itemId;
    this.name = name;
    this.cost = cost;
    this.price = price;
    this.voided = voided;
    this.updatedAt = DateTime.parse(updatedAt);
    this.createdAt = DateTime.parse(createdAt);
  }
}
