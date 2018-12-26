package mcmaster.reporting.posfetch;

import mcmaster.reporting.posfetch.data.Business;
import mcmaster.reporting.posfetch.data.Businesses;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Data fetcher that translates JSON into {@link Business} domain classes.
 */
public class BusinessDataFetcher extends DataFetcher<Business> {
  private static final String REQUEST = "/businesses";

  @Inject
  BusinessDataFetcher(RequestHandler requestHandler) {
    super(requestHandler, REQUEST);
  }

  @Override
  List<Business> translateData(String datum) throws IOException {
    return OBJECT_MAPPER.readValue(datum, Businesses.class).getBusinesses();
  }
}
