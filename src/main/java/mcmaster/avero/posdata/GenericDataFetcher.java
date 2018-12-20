package mcmaster.avero.posdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;
import com.google.common.collect.ImmutableMap;
import mcmaster.avero.data.CountData;
import mcmaster.avero.data.GenericItem;

import java.io.IOException;
import java.util.List;
import java.util.Map;

abstract class GenericDataFetcher<T extends GenericItem> {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final int MAX_LIMIT = 500;
  private final RequestHandler requestHandler;
  private final FetchUrl url;

  GenericDataFetcher(RequestHandler requestHandler, String request) {
    this.requestHandler = requestHandler;
    this.url = new FetchUrl(request);
  }

  Map<String, T> getData() throws IOException {
    ImmutableMap.Builder<String, T> result = ImmutableMap.builder();
    String content = requestHandler.sendRequest(url);
    CountData countData = new ObjectMapper().readValue(content, CountData.class);
    int itemsToFetch = countData.getCount();
    while (itemsToFetch > 0) {
      translateData(content).forEach((d) -> result.put(d.getId(), d));
      url.offset += MAX_LIMIT;
      itemsToFetch -= MAX_LIMIT;
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
