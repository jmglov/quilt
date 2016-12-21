(ns quilt.code)

(defn add [code form]
  (conj code
        (assoc form :index (count code))))
