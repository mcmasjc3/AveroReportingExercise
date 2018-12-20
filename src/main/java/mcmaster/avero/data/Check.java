package mcmaster.avero.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class Check implements GenericItem {
  private final String id;
  private final String businessId;
  private final String employeeId;
  private final String name;
  private final boolean closed;
  private final DateTime closedAt;
  private final DateTime updatedAt;
  private final DateTime createdAt;

  @JsonCreator
  public Check(
      @JsonProperty("id") String id,
      @JsonProperty("business_id") String businessId,
      @JsonProperty("employee_id") String employeeId,
      @JsonProperty("name") String name,
      @JsonProperty("closed") boolean closed,
      @JsonProperty("closed_at") String closedAt,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("created_at") String createdAt) {
    this.id = id;
    this.businessId = businessId;
    this.employeeId = employeeId;
    this.name = name;
    this.closed = closed;
    this.closedAt = DateTime.parse(closedAt);
    this.updatedAt = DateTime.parse(updatedAt);
    this.createdAt = DateTime.parse(createdAt);
  }

  public String getId() {
    return id;
  }

  public String getBusinessId() {
    return businessId;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public String getName() {
    return name;
  }

  public boolean isClosed() {
    return closed;
  }

  public DateTime getClosedAt() {
    return closedAt;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }
}
