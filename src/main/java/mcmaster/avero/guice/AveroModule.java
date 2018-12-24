package mcmaster.avero.guice;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import mcmaster.avero.posfetch.BusinessDataFetcher;
import mcmaster.avero.posfetch.CheckDataFetcher;
import mcmaster.avero.posfetch.EmployeeDataFetcher;
import mcmaster.avero.posfetch.LaborEntryDataFetcher;
import mcmaster.avero.posfetch.MenuItemDataFetcher;
import mcmaster.avero.posfetch.OrderedItemDataFetcher;
import mcmaster.avero.posfetch.data.Business;
import mcmaster.avero.posfetch.data.Check;
import mcmaster.avero.posfetch.data.Employee;
import mcmaster.avero.posfetch.data.LaborEntry;
import mcmaster.avero.posfetch.data.MenuItem;
import mcmaster.avero.posfetch.data.OrderedItem;

import java.util.Map;

/**
 * Guice module for Avero reporting server.
 */
public class AveroModule extends AbstractModule {

  @Override
  public void configure() {
    bind(HttpTransport.class).to(NetHttpTransport.class);
  }

  @Provides
  Map<String, Business> provideBusinesses(BusinessDataFetcher fetcher) {
    return fetcher.getData();
  }

  @Provides
  Map<String, Check> provideChecks(CheckDataFetcher fetcher) {
    return fetcher.getData();
  }

  @Provides
  Map<String, Employee> provideEmployees(EmployeeDataFetcher fetcher) {
    return fetcher.getData();
  }

  @Provides
  Map<String, LaborEntry> provideLaborEntriess(LaborEntryDataFetcher fetcher) {
    return fetcher.getData();
  }

  @Provides
  Map<String, MenuItem> provideMenuItems(MenuItemDataFetcher fetcher) {
    return fetcher.getData();
  }

  @Provides
  Map<String, OrderedItem> provideOrderedItems(OrderedItemDataFetcher fetcher) {
    return fetcher.getData();
  }
}
