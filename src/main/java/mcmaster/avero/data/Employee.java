package mcmaster.avero.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class Employee implements GenericItem {
  private final String id;
  private final String businessId;
  private final String employeeId;
  private final String firstName;
  private final String lastName;
  private final int payRate;
  private final DateTime updatedAt;
  private final DateTime createdAt;

  @JsonCreator
  public Employee(
      @JsonProperty("id") String id,
      @JsonProperty("business_id") String businessId,
      @JsonProperty("employee_id") String employeeId,
      @JsonProperty("first_name") String firstName,
      @JsonProperty("last_name") String lastName,
      @JsonProperty("pay_rate") int payRate,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("created_at") String createdAt) {
    this.id = id;
    this.businessId = businessId;
    this.employeeId = employeeId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.payRate = payRate;
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

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
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
