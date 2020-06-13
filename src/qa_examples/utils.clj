(ns qa-examples.utils
  (:require [java-time :as time]
            [incanter.stats :as st]
            [incanter.core :as i]
            [clojure.core.matrix.dataset :as ds])
  (:import (java.util Calendar TimeZone List)
           (java.time ZonedDateTime)
           (org.apache.commons.lang3.time DateUtils)
           (yahoofinance.histquotes HistoricalQuote)))


(defn nyse-market-open-date-time [y m d]
  (-> (time/zoned-date-time y m d 9 30)
      (time/with-zone "GMT-6")))

(defn nyse-market-close-date-time [y m d]
  (-> (time/zoned-date-time y m d 16)
      (time/with-zone "GMT-6")))

(defn calendar->zoned-date-time [^Calendar calendar]
  (time/zoned-date-time (-> calendar (.get Calendar/YEAR))
                        (-> calendar (.get Calendar/MONTH) inc)
                        (-> calendar (.get Calendar/DAY_OF_MONTH))))

(defn zoned-date-time->calendar [^ZonedDateTime date-time]
  (doto (-> (time/java-date date-time)
            (DateUtils/toCalendar))
    (.setTimeZone (TimeZone/getTimeZone (-> date-time .getZone .getId)))))

(defn sharpe
  ([dataset]
   (let [excess-ret (drop 1 (i/$ :excess-ret dataset))]
     (i/$= (i/sqrt 252) * (st/mean excess-ret) / (st/sd excess-ret))))
  ([dataset excess-ret-kw]
   (let [excess-ret (drop 1 (i/$ excess-ret-kw dataset))]
     (i/$= (i/sqrt 252) * (st/mean excess-ret) / (st/sd excess-ret)))))

(def cols [:date :open :high :low :close :volume :adj-close :daily-ret :excess-ret])

(defn extract-data [^List quotes]
  (let [date (map #(->> ^HistoricalQuote %
                        .getDate
                        calendar->zoned-date-time
                        (time/format "dd/MM/yyyy")) quotes)
        open (mapv #(-> ^HistoricalQuote % .getOpen .doubleValue) quotes)
        close (mapv #(double (-> ^HistoricalQuote % .getClose .doubleValue)) quotes)
        high (mapv #(double (-> ^HistoricalQuote % .getHigh .doubleValue)) quotes)
        low (mapv #(-> ^HistoricalQuote % .getLow .doubleValue) quotes)
        volume (mapv #(-> ^HistoricalQuote % .getVolume .doubleValue) quotes)
        adj-close (mapv #(-> ^HistoricalQuote % .getAdjClose .doubleValue) quotes)
        daily-ret (concat [nil] (mapv (fn [curr-close prev-close]
                                        (i/$= (curr-close - prev-close) /
                                              prev-close))
                                      (drop 1 adj-close)
                                      (drop-last adj-close)))
        excess-ret (concat [nil] (mapv #(- % (/ 0.04 252)) (drop 1 daily-ret)))]
    {:date       date
     :open       open
     :high       high
     :low        low
     :close      close
     :volume     volume
     :adj-close  adj-close
     :daily-ret  daily-ret
     :excess-ret excess-ret}))

(defn rename-cols [ds new-cols]
  (ds/rename-columns ds (mapv (fn [k v] [k v]) cols new-cols)))

(defn quotes->ds
  ([quotes] (ds/dataset cols (extract-data quotes)))
  ([quotes col-names] (-> (ds/dataset cols (extract-data quotes))
                          (rename-cols col-names))))