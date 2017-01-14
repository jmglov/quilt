(ns quilt.sketch
  (:require [clojure.string :as string]
            [quilt.color :refer [->html-color]]
            [quilt.util :as util :refer [concatv]]
            [re-frame.core :as rf]))

;; http://stackoverflow.com/a/18473154/58994

(defn- polar->cartesian [[x y] radius angle-deg]
  (let [angle-rad (/ (* (- angle-deg 90) Math/PI) 180.0)]
    [(+ x (int (* radius (Math/cos angle-rad))))
     (+ y (int (* radius (Math/sin angle-rad))))]))

(defn- make-circle [[x y] radius color]
  [:circle {:cx x
            :cy y
            :r radius
            :fill (->html-color color)
            :stroke-width 0}])

(defn- make-curve
  [pos radius orientation thickness color]
  (let [[angle1 angle2] (case orientation
                          :down [270 90]
                          :left [0 180]
                          :right [180 0]
                          :up [90 270])
        [x1 y1] (polar->cartesian pos radius angle1)
        [x2 y2] (polar->cartesian pos radius angle2)
        arc-flag "0"]
    [:path {:stroke (->html-color color)
            :stroke-width thickness
            :fill-opacity 0
            :d (->> ["M" x1 y1
                     "A" radius radius 0 arc-flag 0 x2 y2]
                    (string/join " "))}]))

(defn- make-line
  [[[x1 y1] [x2 y2]] thickness color]
  [:line {:x1 x1, :y1 y1
          :x2 x2, :y2 y2
          :stroke (->html-color color)
          :stoke-width thickness}])

(defn- make-rectangle [[x y] width height color]
  [:rect {:x x, :y y
          :width width, :height height
          :fill (->html-color color)
          :stroke-width 0}])

(defn- make-text [text [x y] size color]
  [:text {:x x, :y y
          :font-size size
          :fill (->html-color color)
          :text-anchor :middle}
   text])

(defn- make-triangle
  [position color]
  (let [points (->> position
                    (map #(string/join "," %))
                    (string/join " "))]
    [:polygon {:points points
               :fill (->html-color color)}]))

(defn- ->shape [{:keys [fun color] :as form}]
  (case fun
    :circle
    (let [{:keys [position radius]} form]
      (make-circle position radius color))

    :curve
    (let [{:keys [position radius thickness orientation]} form]
      (make-curve position radius orientation thickness color))

    :line
    (let [{:keys [position thickness]} form]
      (make-line position thickness color))

    :rectangle
    (let [{:keys [position width height]} form]
      (make-rectangle position width height color))

    :text
    (let [{:keys [text position size]} form]
      (make-text text position size color))

    :triangle
    (let [{:keys [position]} form]
      (make-triangle position color))

    nil))

(defn- get-mouse-pos [event]
  (util/relative-position event (util/get-element "sketch")))

(defn sketch []
  (let [code-atom (rf/subscribe [:code])
        sketch-atom (rf/subscribe [:sketch])]
    (fn []
      (let [[width height] (:size @sketch-atom)]
        (concatv
         [:svg#sketch {:width width
                       :height height
                       :on-click #(rf/dispatch [:lock-mouse-pos])
                       :on-mouseMove #(rf/dispatch [:set-mouse-pos
                                                    (get-mouse-pos %)])}
          (make-rectangle [0 0] width height (:bg-color @sketch-atom))]
         (mapv ->shape @code-atom))))))
