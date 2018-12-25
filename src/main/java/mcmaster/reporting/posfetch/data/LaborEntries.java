package mcmaster.reporting.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Domain class representing a batch of labor entries fetched from the POS system. Created from JSON
 * in production.
 */
public class LaborEntries {
  private final int count;
  private final List<LaborEntry> laborEntries;

  @JsonCreator
  public LaborEntries(
      @JsonProperty("count") int count, @JsonProperty("data") List<LaborEntry> laborEntries) {
    this.count = count;
    this.laborEntries = laborEntries;
  }

  public int getCount() {
    return count;
  }

  public List<LaborEntry> getLaborEntries() {
    return laborEntries;
  }
}
