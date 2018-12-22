package mcmaster.avero.posfetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;
import com.google.common.collect.ImmutableMap;
import mcmaster.avero.posfetch.data.CountData;
import mcmaster.avero.posfetch.data.GenericItem;

import java.io.IOException;
import java.util.List;
import java.util.Map;

abstract class AbstractDataFetcher<T extends GenericItem> {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final int MAX_LIMIT = 500;
  private final RequestHandler requestHandler;
  private final FetchUrl url;

  AbstractDataFetcher(RequestHandler requestHandler, String request) {
    this.requestHandler = requestHandler;
    this.url = new FetchUrl(request);
  }

  public Map<String, T> getData() {
    ImmutableMap.Builder<String, T> result = ImmutableMap.builder();
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

  abstract List<T> translateData(String datum) throws IOException;

  private static class FetchUrl extends GenericUrl {
    @Key
    private int limit = MAX_LIMIT;
    @Key
    private int offset = 0;

    private FetchUrl(String request) {
      super(RequestHandler.BASE_URL + request);
    }
  }
}
