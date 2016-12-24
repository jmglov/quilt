(ns quilt.library
  "Built-in library of sketches")

(def sketches
  {:hello-world [{:fun :text
                  :color :black
                  :size 48
                  :position [320 50]
                  :text "Hello world!"}
                 {:fun :circle
                  :color :yellow
                  :radius 100
                  :position [320 240]}
                 {:fun :circle
                  :color :black
                  :radius 10
                  :position [280 200]}
                 {:fun :circle
                  :color :black
                  :radius 10
                  :position [360 200]}
                 {:fun :curve
                  :thickness 15
                  :color :black
                  :orientation :down
                  :position [[260 250] [380 250]]}]})
