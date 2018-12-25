package mcmaster.reporting.server;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeConverter;
import mcmaster.reporting.posfetch.BusinessDataFetcher;
import mcmaster.reporting.posfetch.CheckDataFetcher;
import mcmaster.reporting.posfetch.EmployeeDataFetcher;
import mcmaster.reporting.posfetch.LaborEntryDataFetcher;
import mcmaster.reporting.posfetch.MenuItemDataFetcher;
import mcmaster.reporting.posfetch.OrderedItemDataFetcher;
import mcmaster.reporting.posfetch.data.Business;
import mcmaster.reporting.posfetch.data.Check;
import mcmaster.reporting.posfetch.data.DomainData;
import mcmaster.reporting.posfetch.data.Employee;
import mcmaster.reporting.posfetch.data.LaborEntry;
import mcmaster.reporting.posfetch.data.MenuItem;
import mcmaster.reporting.posfetch.data.OrderedItem;

import java.util.Map;

/**
 * Guice module for Avero reporting server.
 */
public class ReportModule extends AbstractModule {

  @Override
  public void configure() {
    bind(HttpTransport.class).to(NetHttpTransport.class);
    configureDomainData();
  }

  @Override
  protected void convertToTypes(
      Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
    super.convertToTypes(typeMatcher, converter);
  }

  /**
   * Bind {@link DomainData} eagerly during server startup.
   *
   * <p>Otherwise it will be loaded on the first "/reporting" request, which would make that request
   * very slow. We can override this in test modules to load lazily, which makes tests that don't
   * inject DomainData faster.
   */
  protected void configureDomainData() {
    bind(DomainData.class).asEagerSingleton();
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
