(defproject quilt "0.1.0-SNAPSHOT"
  :description "A ClojureScript app designed to teach kids how to program"
  :url "http://jmglov.net/quilt/"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [com.cemerick/url "0.1.1"]
                 [hiccups "0.3.0"]
                 [re-frame "0.9.0"]
                 [reagent "0.6.0"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :min-lein-version "2.7.1"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles {:dev
             {:dependencies [[binaryage/devtools "0.8.2"]
                             [figwheel-sidecar "0.5.7"]
                             [com.cemerick/piggieback "0.2.1"]]

              :plugins [[lein-figwheel "0.5.7"]
                        [lein-doo "0.1.7"]]}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "quilt.core/mount-root"}
                        :compiler {:main quilt.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true
                                   :preloads [devtools.preload]
                                   :external-config {:devtools/config {:features-to-install :all}}}}
                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main quilt.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :closure-defines {goog.DEBUG false}
                                   :pretty-print false}}
                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs"]
                        :compiler {:main quilt.runner
                                   :output-to "resources/public/js/compiled/test.js"
                                   :output-dir "resources/public/js/compiled/test/out"
                                   :optimizations :none}}]})
