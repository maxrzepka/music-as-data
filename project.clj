(defproject music-as-data "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [overtone "0.7.1"]
                 [ring/ring-core "1.1.6"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [net.cgrand/moustache "1.1.0"]
                 [enlive "1.0.1"]]
  :plugins [[lein-cljsbuild "0.2.10"]
            [lein-ring "0.7.5"]]
  :cljsbuild {:builds [{:source-path "src/cljs"
                        :compiler {:output-to "resources/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true
                                   }}]})
