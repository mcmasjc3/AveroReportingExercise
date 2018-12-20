package mcmaster.avero.posdata;

import mcmaster.avero.data.LaborEntries;
import mcmaster.avero.data.LaborEntry;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

class LaborEntryDataFetcher extends GenericDataFetcher<LaborEntry> {
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
