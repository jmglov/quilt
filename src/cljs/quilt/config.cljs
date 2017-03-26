(ns quilt.config)

(def debug? ^boolean js/goog.DEBUG)

(def default-sketch-size [500 500])
(def bg-color [235 235 224])

;; These maximums only apply to randomly generated sketches
(def max-x (first default-sketch-size))
(def max-y (second default-sketch-size))

(def max-width max-x)
(def max-height max-y)

(def max-radius (int (/ max-height 2)))

(def max-thickness (int (/ max-height 4)))

(def max-text-size 128)
