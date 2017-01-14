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
                  :position [250 250]
                  :radius 80
                  :orientation :down
                  :thickness 10
                  :color :black}]
   :suit [{:fun :line
           :color :black
           :position [[0 70] [500 70]]
           :thickness 10}
          {:fun :text
           :color :black
           :position [250 10]
           :text "Hello, world!"
           :size 40}
          {:fun :circle
           :color :yellow
           :position [250 270]
           :radius 150}
          {:fun :circle
           :color :black
           :position [200 220]
           :radius 10}
          {:fun :circle
           :color :black
           :position [300 220]
           :radius 10}
          {:fun :curve
           :color :black
           :position [250 300]
           :radius 75
           :orientation :down
           :thickness 10}
          {:fun :rectangle
           :color :black
           :position [0 420]
           :width 500
           :height 500}
          {:fun :triangle
           :color :red
           :position [[220 420] [280 420] [250 500]]}]})
