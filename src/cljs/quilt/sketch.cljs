(ns quilt.sketch
  (:require [clojure.string :as string]
            [goog.dom :as dom]
            [quilt.color :as q.color]
            [quilt.util :refer [concatv]]
            [re-frame.core :as rf]
            [reagent.core :as r])
  (:require-macros [cljs.core.async.macros :as a]))

(defn- hex-color [[r g b]]
  (str "#" (.toString r 16) (.toString g 16) (.toString b 16)))

(defn- set-color! [c]
  #_(let [[r g b] (if (keyword? c) (q.color/color c) c)]
      (q/fill r g b)
      (q/stroke r g b)))

(defn- clear! [sketch]
  #_(let [{:keys [bg-color]} sketch]
      (apply q/background bg-color)))

(defn- draw-circle! [[x y] radius color]
  (set-color! color)
  (let [circumference (* 2 radius)]
    #_(q/ellipse x y circumference circumference)))

(defn- curve-control-points [[[x1 y1] [x2 y2]] orientation [w h]]
  (case orientation
    :down [[x1 0] [x2 0]]
    :up [[x1 h] [x2 h]]
    :left [[0 y1] [0 y2]]
    :right [[w y1] [w y2]]))

(defn- draw-curve!
  [[[x1 y1] [x2 y2] :as position] orientation thickness color sketch-size]
  #_(let [[[cx1 cy1] [cx2 cy2]] (curve-control-points position
                                                      orientation
                                                      sketch-size)]
      (q/no-fill)
      (q/stroke-weight thickness)
      (apply q/stroke (q.color/color color))
      (q/curve x1 cy1 x1 y1 x2 y2 cx2 cy2)
      (q/stroke-weight 1)
      (q/fill :black)))

(defn- draw-line!
  [[[x1 y1] [x2 y2]] thickness color]
  #_(do
      (q/stroke-weight thickness)
      (apply q/stroke (q.color/color color))
      (q/line x1 y1 x2 y2)
      (q/stroke-weight 1)))

(defn- rectangle [[x y] width height color]
  [:rect {:width width
          :height height
          :style {:fill (hex-color color)
                  :stroke-width 0}}])

(defn- draw-text! [text [x y] size color]
  (set-color! color)
  #_(do (q/text-size size)
        (q/text-align :center :top)
        (q/text text x y)))

(defn- draw-triangle!
  [[[x1 y1] [x2 y2] [x3 y3]] color]
  (set-color! color)
  #_(q/triangle x1 y1 x2 y2 x3 y3))

(defn- draw! [sketch-atom code-atom]
  (clear! @sketch-atom)
  (doseq [{:keys [fun color] :as form} @code-atom]
    (case fun
      :circle
      (let [{:keys [position radius]} form]
        (draw-circle! position radius color))

      :curve
      (let [{:keys [position thickness orientation]} form]
        (draw-curve! position orientation thickness color
                     (:size @sketch-atom)))

      :line
      (let [{:keys [position thickness]} form]
        (draw-line! position thickness color))

      :rectangle
      (let [{:keys [position width height]} form]
        (rectangle position width height color))

      :text
      (let [{:keys [text position size]} form]
        (draw-text! text position size color))

      :triangle
      (let [{:keys [position]} form]
        (draw-triangle! position color))

      nil)))

(defn- get-mouse-pos [event]
  (let [canvas-rect (.getBoundingClientRect (.-target event))
        x (- (.-clientX event) (.-left canvas-rect))
        y (- (.-clientY event) (.-top canvas-rect))]
    [x y]))

(defn sketch []
  (let [code-atom (rf/subscribe [:code])
        sketch-atom (rf/subscribe [:sketch])]
    (fn []
      (let [[width height] (:size @sketch-atom)]
        [:svg {:width width
               :height height
               :on-click #(rf/dispatch [:lock-mouse-pos])
               :on-mouseMove #(rf/dispatch [:set-mouse-pos
                                            (get-mouse-pos %)])}
         (rectangle [0 0] width height (:bg-color @sketch-atom))]))))
