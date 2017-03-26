(ns quilt.spec
  (:require [cljs.spec :as s]
            [quilt.color :as color]
            [quilt.config :as config]))

;; The maximums only apply to randomly generated sketches, so these specs are
;; intended more for generation than validation. Maybe I'll fix this by making
;; some custom generators.

(s/def :form/color (set (keys color/color)))
(s/def :form/position (s/cat :x (s/int-in 0 config/max-x)
                             :y (s/int-in 0 config/max-y)))

(s/def :circle/fun #{:circle})
(s/def :circle/radius (s/int-in 1 config/max-radius))

(s/def :curve/fun #{:curve})
(s/def :curve/orientation #{:up :down :left :right})

(s/def :line/fun #{:line})
(s/def :line/position (s/and (s/coll-of :form/position)
                             #(= 2 (count %))))
(s/def :line/thickness (s/int-in 1 config/max-thickness))

(s/def :rectangle/fun #{:rectangle})
(s/def :rectangle/width (s/int-in 1 config/max-width))
(s/def :rectangle/height (s/int-in 1 config/max-height))

(s/def :text/fun #{:text})
(s/def :text/size (s/int-in 1 config/max-text-size))
(s/def :text/text (s/and string? not-empty))

(s/def :triangle/fun #{:triangle})
(s/def :triangle/position (s/and (s/coll-of :form/position)
                                 #(= 3 (count %))))

(s/def :form/shared (s/keys :req-un [:form/color
                                     :form/position]))

(s/def :form/circle (s/merge :form/shared
                             (s/keys :req-un [:circle/fun
                                              :circle/radius])))

(s/def :form/curve (s/merge :form/shared
                            (s/keys :req-un [:curve/fun
                                             :curve/orientation
                                             :circle/radius
                                             :line/thickness])))

(s/def :form/line (s/keys :req-un [:line/fun
                                   :form/color
                                   :line/position
                                   :line/thickness]))

(s/def :form/rectangle (s/merge :form/shared
                                (s/keys :req-un [:rectangle/fun
                                                 :rectangle/height
                                                 :rectangle/width])))

(s/def :form/text (s/merge :form/shared
                           (s/keys :req-un [:text/fun
                                            :text/size
                                            :text/text])))

(s/def :form/triangle (s/keys :req-un [:triangle/fun
                                       :triangle/position
                                       :form/color]))

(s/def :form/form (s/or :circle :form/circle
                        :curve :form/curve
                        :line :form/line
                        :rectangle :form/rectangle
                        :text :form/text
                        :triangle :form/triangle))
