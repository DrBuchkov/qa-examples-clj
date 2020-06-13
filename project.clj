(defproject qa_examples "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clojure.java-time "0.3.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.fzakaria/slf4j-timbre "0.3.19"]
                 ;; https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
                 [org.apache.commons/commons-lang3 "3.10"]
                 ;; https://mvnrepository.com/artifact/com.yahoofinance-api/YahooFinanceAPI
                 [incanter "1.9.3"]
                 [com.yahoofinance-api/YahooFinanceAPI "3.15.0"]
                 ;; https://mvnrepository.com/artifact/org.ta4j/ta4j-core
                 [org.ta4j/ta4j-core "0.13"]]
  ;:jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]
  :main ^:skip-aot qa-examples.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
