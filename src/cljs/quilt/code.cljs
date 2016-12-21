(ns quilt.code
  (:refer-clojure :exclude [replace]))

(defn add [code form]
  (let [form (assoc form :index (count code))]
    (println "Adding code:" form)
    (conj code form)))

(defn replace [code {:keys [index] :as form}]
  (println "Replacing code at index" index)
  (println "Old:" (get code index))
  (println "New:" form)
  (assoc code index form))
