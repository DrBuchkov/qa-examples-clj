(ns qa-examples.example-3-4
  (:use [incanter.io])
  (:require [qa-examples.utils :refer [zoned-date-time->calendar
                                       calendar->zoned-date-time
                                       nyse-market-close-date-time
                                       nyse-market-open-date-time
                                       sharpe
                                       quotes->ds]]
            [incanter.core :as i]
            [clojure.core.matrix.dataset :as ds])
  (:import (yahoofinance YahooFinance)
           (yahoofinance.histquotes Interval)))


(def ige-cols [:date :open-ige :high-ige :low-ige :close-ige :volume-ige :adj-close-ige :daily-ret-ige :excess-ret-ige])

(def spy-cols [:date :open-spy :high-spy :low-spy :close-spy :volume-spy :adj-close-spy :daily-ret-spy :excess-ret-spy])

(def ige-quotes
  (let [from (zoned-date-time->calendar (nyse-market-open-date-time 2001 11 26))
        to (zoned-date-time->calendar (nyse-market-close-date-time 2007 11 14))
        stock (YahooFinance/get "IGE")]
    (.getHistory stock from to Interval/DAILY)))

(def spy-quotes
  (let [from (zoned-date-time->calendar (nyse-market-close-date-time 2001 11 26))
        to (zoned-date-time->calendar (nyse-market-open-date-time 2007 11 14))
        stock (YahooFinance/get "SPY")]
    (.getHistory stock from to Interval/DAILY)))

(def ige-ds (quotes->ds ige-quotes ige-cols))

(def spy-ds (quotes->ds spy-quotes spy-cols))

(def long-short-ds (i/$join [:date :date] ige-ds spy-ds))
