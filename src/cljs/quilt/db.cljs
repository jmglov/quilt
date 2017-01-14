(ns quilt.db)

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
            :size [500 500]
            :lo-res? false
            :bg-color [235 235 224]
            :fg-color [0 0 0]}})
