(ns quilt.code
  (:refer-clojure :exclude [replace]))

(defn add [code form]
  (let [form (assoc form :index (count code))]
    (conj code form)))

(defn replace [code {:keys [index] :as form}]
  (assoc code index form))

(defn create-form [fun]
  (merge {:fun fun}
         (case fun
           :clear {}
           :color {:color [0 0 0]}
           :circle {:position [0 0], :radius 0}
           :text {:text "", :position [0 0], :size 24})))
