package mcmaster.avero.posdata;

import mcmaster.avero.data.Check;
import mcmaster.avero.data.Checks;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

class CheckDataFetcher extends GenericDataFetcher<Check> {
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
