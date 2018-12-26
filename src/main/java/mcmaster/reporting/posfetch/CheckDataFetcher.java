package mcmaster.reporting.posfetch;

import mcmaster.reporting.posfetch.data.Check;
import mcmaster.reporting.posfetch.data.Checks;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Data fetcher that translates JSON into {@link Check} domain classes.
 */
public class CheckDataFetcher extends DataFetcher<Check> {
  private static final String REQUEST = "/checks";

  @Inject
  CheckDataFetcher(RequestHandler requestHandler) {
    super(requestHandler, REQUEST);
  }

  @Override
  List<Check> translateData(String datum) throws IOException {
    return OBJECT_MAPPER.readValue(datum, Checks.class).getChecks();
  }
}
