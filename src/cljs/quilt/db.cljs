(ns quilt.db
  (:require [quilt.config :as config]))

(def default-db
  {:code []
   :source ""
   :editor {:type :visual
            :displayed-docstring nil
            :selected-index nil
            :debug? false
            :readonly? false}
   :mouse {:locked? false
           :pos [0 0]}
   :sketch {:name "sketch"
            :size config/default-sketch-size
            :lo-res? true
            :bg-color config/bg-color
            :fg-color [0 0 0]}})
