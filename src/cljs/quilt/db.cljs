(ns quilt.db)

(def default-db
  {:code []
   :source ""
   :editor {:type :visual
            :debug? false}
   :mouse {:locked? false
           :pos [0 0]}
   :sketch {:name "sketch"
            :size [500 500]
            :bg-color [235 235 224]
            :fg-color [0 0 0]}})
