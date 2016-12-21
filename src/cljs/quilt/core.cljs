(ns quilt.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as q.middleware]
            [quilt.config :as config]
            [quilt.events]
            [quilt.subs]
            [quilt.views :as views]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn- quil-setup []
  {})

(defn- quil-update [state]
  state)

(defn- quil-draw! [state])

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
