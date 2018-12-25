package mcmaster.reporting.posfetch;

import mcmaster.reporting.posfetch.data.LaborEntries;
import mcmaster.reporting.posfetch.data.LaborEntry;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Data fetcher that translates JSON into {@link LaborEntry} domain classes.
 */
public class LaborEntryDataFetcher extends AbstractDataFetcher<LaborEntry> {
  private static final String REQUEST = "/laborentries";

  @Inject
  LaborEntryDataFetcher(RequestHandler requestHandler) {
    super(requestHandler, REQUEST);
  }

  @Override
  List<LaborEntry> translateData(String datum) throws IOException {
    return OBJECT_MAPPER.readValue(datum, LaborEntries.class).getLaborEntries();
  }
}
