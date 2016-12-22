(ns quilt.code
  (:require [quilt.color :as color])
  (:refer-clojure :exclude [replace]))

(def functions [:circle
                :curve
                :text])

(defn add [code form]
  (let [form (assoc form :index (count code))]
    (conj code form)))

(defn delete [code {:keys [index]}]
  (->> code
       (remove #(= index (:index %)))
       (into [])))

(defn replace [code {:keys [index] :as form}]
  (assoc code index form))

(defn create-form [fun]
  (merge {:fun fun
          :color color/default}
         (case fun
           :circle {:position [0 0]
                    :radius 0}
           :curve {:position [[0 0] [0 0]]
                   :control [[0 0] [0 0]]
                   :thickness 10}
           :text {:text ""
                  :position [0 0]
                  :size 24})))
