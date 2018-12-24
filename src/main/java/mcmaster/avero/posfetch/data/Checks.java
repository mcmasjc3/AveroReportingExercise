package mcmaster.avero.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Domain class representing a batch of checks fetched from the POS system. Created from JSON in
 * production.
 */
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
