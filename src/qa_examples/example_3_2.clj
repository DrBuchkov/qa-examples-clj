(ns qa-examples.example-3-2
  (:require [clojure.pprint :as pp]
            [qa-examples.utils :refer [zoned-date-time->calendar
                                       calendar->zoned-date-time
                                       nyse-market-close-date-time
                                       nyse-market-open-date-time]]
            [java-time :as time])
  (:import (yahoofinance YahooFinance)
           (yahoofinance.histquotes Interval HistoricalQuote)))

(def ige-quotes
  (let [from (zoned-date-time->calendar (nyse-market-open-date-time 2005 6 7))
        to (zoned-date-time->calendar (nyse-market-close-date-time 2005 6 10))
        stock (YahooFinance/get "IGE")]
    (.getHistory stock from to Interval/DAILY)))

(defn summarize-quote [^HistoricalQuote quote]
  {"Date"           (->> quote
                         .getDate
                         calendar->zoned-date-time
                         (time/format "dd/MM/yyyy"))
   "Open"           (-> quote .getOpen)
   "High"           (-> quote .getHigh)
   "Low"            (-> quote .getLow)
   "Close"          (-> quote .getClose)
   "Volume"         (-> quote .getVolume)
   "Adjusted Close" (-> quote .getAdjClose)})

(pp/print-table (mapv summarize-quote ige-quotes))