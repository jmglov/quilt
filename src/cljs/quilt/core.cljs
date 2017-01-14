(ns quilt.core
  (:require [cljs.core]
            [quilt.config :as config]
            [quilt.events]
            [quilt.subs]
            [quilt.views :as views]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defn dev-setup []
  (if config/debug?
    (enable-console-print!)
    (set! cljs.core/*print-fn* (fn [& _]))))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
