package mcmaster.avero.posfetch;

import mcmaster.avero.posfetch.data.LaborEntries;
import mcmaster.avero.posfetch.data.LaborEntry;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

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
