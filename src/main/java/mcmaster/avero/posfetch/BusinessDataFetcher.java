package mcmaster.avero.posfetch;

import mcmaster.avero.posfetch.data.Business;
import mcmaster.avero.posfetch.data.Businesses;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class BusinessDataFetcher extends AbstractDataFetcher<Business> {
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
