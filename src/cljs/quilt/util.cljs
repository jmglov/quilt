(ns quilt.util)

(defn concatv [& vs]
  (into [] (apply concat vs)))

(defn get-value
  ([element]
   (-> element .-target .-value))
  ([element tx default]
   (-> element get-value tx (or default))))
