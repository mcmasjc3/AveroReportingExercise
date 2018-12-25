package mcmaster.reporting.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Domain class representing a single business. Created from JSON in production.
 */
public class Business extends AbstractItem {
  private final String name;
  private final List<Integer> hours;
  private final DateTime updatedAt;
  private final DateTime createdAt;

  @JsonCreator
  public Business(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("hours") List<Integer> hours,
      @JsonProperty("updated_at") String updatedAt,
      @JsonProperty("created_at") String createdAt) {
    super(id);
    this.name = name;
    this.hours = hours;
    this.updatedAt = DateTime.parse(updatedAt);
    this.createdAt = DateTime.parse(createdAt);
  }

  public String getName() {
    return name;
  }

  public List<Integer> getHours() {
    return hours;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }
}
