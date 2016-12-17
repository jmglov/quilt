(ns quilt.db)

(def default-db
  {:code [{:fun :clear
           :index 0}
          {:fun :color
           :index 1
           :color [0 0 0]}
          {:fun :text
           :index 2
           :position [320 240]
           :size 24
           :text "Hello, world!"}]
   :sketch {:name "sketch"
            :size [640 480]
            :bg-color [235 235 224]}})
