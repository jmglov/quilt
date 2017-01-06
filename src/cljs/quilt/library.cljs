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
                  :position [[160 275] [340 275]]}]
   :suit [{:fun :line,
           :color :black,
           :position [[0 70] [500 70]],
           :thickness 10,
           :index 0}
          {:fun :text,
           :color :black,
           :position [250 10],
           :text "Hello, world!",
           :size 40,
           :index 1}
          {:fun :circle,
           :color :yellow,
           :position [250 270],
           :radius 150,
           :index 2}
          {:fun :circle,
           :color :black,
           :position [200 220],
           :radius 10,
           :index 3}
          {:fun :circle,
           :color :black,
           :position [300 220],
           :radius 10,
           :index 4}
          {:fun :curve,
           :color :black,
           :position [[180 300] [320 300]],
           :orientation :down,
           :thickness 10,
           :index 5}
          {:fun :rectangle,
           :color :black,
           :position [0 420],
           :width 500,
           :height 500,
           :index 6}
          {:fun :triangle,
           :color :red,
           :position [[220 420] [280 420] [250 500]],
           :index 7}]})
