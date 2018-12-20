package mcmaster.avero.posdata;

import mcmaster.avero.data.Business;
import mcmaster.avero.data.Businesses;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

class BusinessDataFetcher extends GenericDataFetcher<Business> {
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
