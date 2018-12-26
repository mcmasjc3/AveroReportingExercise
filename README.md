# Avero Reporting Exercise

## Project Structure
Code is organized into three main packages in main/java.  Corresponding tests are main/java.  Details on class function are in JavaDoc.

### mcmaster.reporting.posfetch
Contains classes that fetch data from the POS API and store them into domain objects representing businesses, employees, etc.  DataFetcher implementations make HTTP requests to API endpoints and use GSON and Jackson2 to parse them into Java objects.  THese are stored in DomainData, which has methods to return instances in the form required to produce reports.

### mcmaster.reporting.reports
Contains two major functions, Bucketer and ReportGenerator.  

Instances of Bucketer are responsible for collecting data into timed Buckets for the time interval (hour, day, etc.) specified for the report.  Buckets span the time span from the beginning to the end of the interval.  If we get an item with a create_time of 09:15, it goes into an hour bucket starting at 09:00, or a day bucket starting at 00:00.

Instances of ReportGenerator use the bucketed data to generate reports.

### mcmaster.reporting.server
Contains the ReportServer implementation and the Guice Module that binds classes needed to instantiate it.

## Assumptions
1. I use created_time for bucketing all items, not updated_time.
1. Buckets (and data members in the reports) span all the time intervals represented by the data.  If we are creating hour buckets for items created at 09:15, 09:20 and 13:01, buckets are created starting at 09:00 and 13:00.
1. When bucketing labor entries for the LCP report, I treat all portions of an hour as if it were a whole hour.  Buckets are created for the portion of the shift that overlaps the time span for the report.  For example, if a shift runs from 09:45 to 17:30 and the report spans 14:30 to 20:00, we bucket the entire pay rate at 14:00, 15:00,  16:00 and 17:00.  I could be more accurate if I allocated a fraction of the pay rate when a fraction of an hour overlaps, but I chose not to add that complication.

## Instructions for running the ReportServer
