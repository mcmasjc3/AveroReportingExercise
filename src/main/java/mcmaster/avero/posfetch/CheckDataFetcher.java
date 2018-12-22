package mcmaster.avero.posfetch;

import mcmaster.avero.posfetch.data.Check;
import mcmaster.avero.posfetch.data.Checks;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class CheckDataFetcher extends AbstractDataFetcher<Check> {
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
