(ns quilt.db
  (:require [cemerick.url :as url]
            [clojure.string :as string]
            [quilt.config :as config]))

(def default-language :sv-SE)
(def default-to-simple-ui? true)

(defn- query-param [param default]
  (-> js/window .-location .-href url/url :query
      (get (name param) default)))

(def default-db
  {:code []
   :source ""
   :language (keyword (query-param :hl default-language))
   :simple-ui? (not= "false" (query-param :simple default-to-simple-ui?))
   :editor {:type :visual
            :displayed-docstring nil
            :selected-index nil
            :debug? false
            :readonly? false}
   :mouse {:locked? false
           :pos [0 0]}
   :sketch {:name "sketch"
            :size config/default-sketch-size
            :bg-color config/bg-color
            :fg-color [0 0 0]}})
