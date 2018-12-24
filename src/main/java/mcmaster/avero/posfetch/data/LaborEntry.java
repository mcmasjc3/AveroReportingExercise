package mcmaster.avero.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

/**
 * Domain class representing a single labor entry. Created from JSON in production.
 */
public class LaborEntry extends AbstractItem {
  private final String businessId;
  private final String employeeId;
  private final String name;
  private final DateTime clockIn;
  private final DateTime clockOut;
  private final int payRate;
  private final DateTime updatedAt;
  private final DateTime createdAt;

  @JsonCreator
  public LaborEntry(
      @JsonProperty("id") String id,
      @JsonProperty("business_id") String businessId,
      @JsonProperty("employee_id") String employeeId,
      @JsonProperty("name") String name,
      @JsonProperty("clock_in") String clockIn,
      @JsonProperty("clock_out") String clockOut,
      @JsonProperty("pay_rate") int payRate,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("created_at") String createdAt) {
    super(id);
    this.businessId = businessId;
    this.employeeId = employeeId;
    this.name = name;
    this.clockIn = DateTime.parse(clockIn);
    this.clockOut = DateTime.parse(clockOut);
    this.payRate = payRate;
    this.updatedAt = DateTime.parse(updatedAt);
    this.createdAt = DateTime.parse(createdAt);
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

  public DateTime getClockIn() {
    return clockIn;
  }

  public DateTime getClockOut() {
    return clockOut;
  }

  public int getPayRate() {
    return payRate;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }
}
