package mcmaster.reporting.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcmaster.reporting.posfetch.data.DomainData;
import org.joda.time.Interval;

import java.util.List;

/**
 * Superclass for all report generators.
 *
 * <p>Subclasses must implement {@link #createReport(ReportType, String, List)} and {@link
 * #generateData(String, Interval, BucketerType)}.
 *
 * @param <R> type of report to generate
 * @param <D> type of data to include in the report.
 */
public abstract class ReportGenerator<R, D> {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  final DomainData domainData;
  private final ReportType reportType;

  ReportGenerator(DomainData domainData, ReportType reportType) {
    this.domainData = domainData;
    this.reportType = reportType;
  }

  /**
   * Template method to generate the requested report by calling subclass methods.
   *
   * @throws InvalidReportException if the requested businessId is not found in the {@link
   *                                DomainData}.
   */
  public String generate(String businessId, Interval reportInterval, BucketerType bucketerType) {
    if (!domainData.getBusiness(businessId).isPresent()) {
      throw new InvalidReportException(String.format("Business %s not found", businessId));
    }
    R report =
        createReport(
            reportType,
            bucketerType.toString(),
            generateData(businessId, reportInterval, bucketerType));
    return GSON.toJson(report);
  }

  /**
   * Method implemented by subclasses to create the report.
   */
  abstract R createReport(ReportType reportType, String timeFrame, List<D> data);

  /**
   * Method implemented by subclasses to create the data for the report.
   */
  abstract List<D> generateData(
      String businessId, Interval reportInterval, BucketerType bucketerType);
}
