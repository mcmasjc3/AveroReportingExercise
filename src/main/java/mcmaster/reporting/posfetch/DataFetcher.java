package mcmaster.reporting.posfetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;
import com.google.common.collect.ImmutableMap;
import mcmaster.reporting.posfetch.data.AbstractItem;
import mcmaster.reporting.posfetch.data.CountData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Superclass for classes that fettch data from the POS system.
 *
 * @param <I> type of item to be fetched.
 */
abstract class DataFetcher<I extends AbstractItem> {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String BASE_URL = "https://secret-lake-26389.herokuapp.com";
  private static final int MAX_LIMIT = 500;
  private final RequestHandler requestHandler;
  private final FetchUrl url;

  DataFetcher(RequestHandler requestHandler, String request) {
    this.requestHandler = requestHandler;
    this.url = new FetchUrl(request);
  }

  /**
   * Returns a {@link Map} of items of the requested type, indexed by id.
   *
   * <p>Subclasses must implement {@link #translateData(String)} to parse JSON into the appropriate
   * domain object.
   *
   * <p>Since the API limits the number of items fetched to a maximum of 500, we may need to send
   * multiple requests to get them all. We get the number of items to fetch from the first request.
   * At each iteration, we request 500, then subtract 500 from itemsToFetch and add 500 to the
   * offset. We keep going as long as itemsToFetch is greater than zero.
   */
  public Map<String, I> getData() {
    ImmutableMap.Builder<String, I> result = ImmutableMap.builder();
    String content;
    CountData countData;
    try {
      content = requestHandler.sendRequest(url);
      countData = new ObjectMapper().readValue(content, CountData.class);
    } catch (IOException e) {
      throw new PosDataException("Unable to read POS data", e);
    }
    int itemsToFetch = countData.getCount();
    while (true) {
      try {
        translateData(content).forEach((d) -> result.put(d.getId(), d));
      } catch (IOException e) {
        throw new PosDataException("Unable to parse POS data", e);
      }
      url.offset += MAX_LIMIT;
      itemsToFetch -= MAX_LIMIT;
      if (itemsToFetch <= 0) {
        break;
      }
      try {
        content = requestHandler.sendRequest(url);
      } catch (IOException e) {
        throw new PosDataException("Unable to read POS data", e);
      }
    }
    return result.build();
  }

  /**
   * Method implemented by subclasses to translate JSON into domain objects.
   */
  abstract List<I> translateData(String datum) throws IOException;

  /**
   * {@link GenericUrl} implementation that adds limit and offset parameters.
   */
  private static class FetchUrl extends GenericUrl {
    @Key
    private int limit = MAX_LIMIT;
    @Key
    private int offset = 0;

    private FetchUrl(String request) {
      super(BASE_URL + request);
    }
  }
}
