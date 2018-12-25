package mcmaster.reporting.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"data"})
public class CountData {
  private final int count;

  @JsonCreator
  public CountData(@JsonProperty("count") int count) {
    this.count = count;
  }

  public int getCount() {
    return count;
  }
}
