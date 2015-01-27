(ns adworj.reporting-test
  (:require [adworj.reporting :as r]
            [clj-time.format :as tf]
            [clojure.test :refer :all])
  (:import [com.google.api.ads.adwords.lib.jaxb.v201409 ReportDefinitionDateRangeType]
           [com.google.api.ads.adwords.lib.jaxb.v201409 ReportDefinitionReportType]))

(deftest report-specification-test
  (let [d (:definition (r/report-specification r/search-query-performance "pauls test"))]
    (is (= ReportDefinitionDateRangeType/YESTERDAY (.getDateRangeType d))))
  (let [start-day (tf/parse "2015-01-01")
        end-day   (tf/parse "2015-01-02")
        d (:definition (r/report-specification r/search-query-performance "custom range test"
                         :range (r/date-range start-day end-day)))]
    (is (= ReportDefinitionDateRangeType/CUSTOM_DATE (.getDateRangeType d)))
    (is (= "20150101" (.. d getSelector getDateRange getMin)))
    (is (= "20150102" (.. d getSelector getDateRange getMax)))
    (is (= 49 (count (.. d getSelector getFields)))))
  (is (= ReportDefinitionReportType/SEARCH_QUERY_PERFORMANCE_REPORT
         (.getReportType (:definition (r/report-specification r/search-query-performance "testing")))))
  (is (false? (.isIncludeZeroImpressions (:definition (r/report-specification r/search-query-performance "testing"))))))

(deftest report-field-mapping-test
  (is (= "AdGroupName"  (get-in r/search-query-performance [:field-mappings :ad-group-name]) )))

(deftest report-field-names-test
  (is (= 49 (count (r/all-fields r/search-query-performance))))
  (is (= :account-currency-code (first (r/all-fields r/search-query-performance)))))
