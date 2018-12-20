package mcmaster.avero.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Checks {
  private final int count;
  private final List<Check> checks;

  @JsonCreator
  public Checks(@JsonProperty("count") int count, @JsonProperty("data") List<Check> checks) {
    this.count = count;
    this.checks = checks;
  }

  public int getCount() {
    return count;
  }

  public List<Check> getChecks() {
    return checks;
  }
}
