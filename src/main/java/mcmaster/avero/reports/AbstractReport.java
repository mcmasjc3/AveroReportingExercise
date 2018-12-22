package mcmaster.avero.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import mcmaster.avero.domain.DomainData;
import org.joda.time.Interval;

import java.util.List;

abstract class AbstractReport<R, D> {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  final DomainData domainData;
  private final String reportType;

  AbstractReport(DomainData domainData, String reportType) {
    this.domainData = domainData;
    this.reportType = reportType;
  }

  String generate(String businessId, Interval reportInterval, BucketerType bucketerType) {
    if (!domainData.getBusiness(businessId).isPresent()) {
      throw new InvalidReportException(String.format("Business %s not found", businessId));
    }
    R report = createReport(reportType, bucketerType.toString(), generateData(businessId, reportInterval,
        bucketerType));
    return new Gson().toJson(report);
  }

  abstract R createReport(String businessId, String timeFrame, List<D> data);

  abstract List<D> generateData(
      String businessId, Interval reportInterval, BucketerType bucketerType);
}
