(ns quilt.views.widgets
  (:require [cljs.reader :refer [read-string]]
            [quilt.util :refer [get-value]]))

(defn input
  ([f size initial-value tx default]
   [:input {:type "text"
            :size size
            :maxLength size
            :value initial-value
            :on-change #(f (get-value % tx default))}]))

(defn input-num [f size initial-value]
  (input f size initial-value read-string 0))

(defn input-text [f size initial-value]
  (input f size initial-value identity ""))
