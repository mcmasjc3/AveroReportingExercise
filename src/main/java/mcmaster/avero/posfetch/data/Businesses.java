package mcmaster.avero.posfetch.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Businesses {
  private final int count;
  private final List<Business> businesses;

  @JsonCreator
  public Businesses(
      @JsonProperty("count") int count, @JsonProperty("data") List<Business> businesses) {
    this.count = count;
    this.businesses = businesses;
  }

  public int getCount() {
    return count;
  }

  public List<Business> getBusinesses() {
    return businesses;
  }
}
