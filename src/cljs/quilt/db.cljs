(ns quilt.db)

(def default-db
  {:code []
   :editor {:type :visual
            :debug? false}
   :sketch {:name "sketch"
            :size [640 480]
            :bg-color [235 235 224]
            :fg-color [0 0 0]}})
