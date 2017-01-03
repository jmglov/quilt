(ns quilt.library
  "Built-in library of sketches")

(def sketches
  {:hello-world [{:fun :text
                  :color :black
                  :size 56
                  :position [250 50]
                  :text "Hello world!"}
                 {:fun :circle
                  :color :yellow
                  :radius 150
                  :position [250 250]}
                 {:fun :circle
                  :color :black
                  :radius 10
                  :position [200 175]}
                 {:fun :circle
                  :color :black
                  :radius 10
                  :position [300 175]}
                 {:fun :curve
                  :thickness 10
                  :color :black
                  :orientation :down
                  :position [[160 275] [340 275]]}]})
