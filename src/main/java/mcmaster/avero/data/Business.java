package mcmaster.avero.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.List;

public class Business implements GenericItem {
  public String getId() {
    return id;
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

  private final String id;
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
    this.id = id;
    this.name = name;
    this.hours = hours;
    this.updatedAt = DateTime.parse(updatedAt);
    this.createdAt = DateTime.parse(createdAt);
  }
}
