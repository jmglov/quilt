(ns quilt.sketch
  (:require [cljs.core.async :as a]
            [goog.dom :as dom]
            [quil.core :as q :include-macros true]
            [quil.sketch :as q.sketch]
            [quilt.color :as q.color]
            [re-frame.core :as re-frame]
            [reagent.core :as r])
  (:require-macros [cljs.core.async.macros :as a]))

(defn- clear! [[r g b]]
  (q/background r g b))

(defn set-color! [c]
  (let [[r g b] (if (keyword? c) (q.color/color c) c)]
    (q/fill r g b)
    (q/stroke r g b)))

(defn draw-circle! [[x y] radius color]
  (set-color! color)
  (let [circumference (* 2 radius)]
    (q/ellipse x y circumference circumference)))

(defn- curve-control-points [[[x1 y1] [x2 y2]] orientation [w h]]
  (case orientation
    :down [[x1 0] [x2 0]]
    :up [[x1 h] [x2 h]]
    :left [[0 y1] [0 y2]]
    :right [[w y1] [w y2]]))

(defn draw-curve!
  [[[x1 y1] [x2 y2] :as position] orientation thickness color sketch-size]
  (let [[[cx1 cy1] [cx2 cy2]] (curve-control-points position
                                                    orientation
                                                    sketch-size)]
    (q/no-fill)
    (q/stroke-weight thickness)
    (apply q/stroke (q.color/color color))
    (q/curve x1 cy1 x1 y1 x2 y2 cx2 cy2)
    (q/stroke-weight 1)
    (q/fill :black)))

(defn draw-line!
  [[[x1 y1] [x2 y2]] thickness color]
  (q/stroke-weight thickness)
  (apply q/stroke (q.color/color color))
  (q/line x1 y1 x2 y2)
  (q/stroke-weight 1))

(defn draw-rectangle! [[x y] width height color]
  (set-color! color)
  (q/rect x y width height))

(defn draw-text! [text [x y] size color]
  (set-color! color)
  (q/text-size size)
  (q/text-align :center :center)
  (q/text text x y))

(defn draw-triangle!
  [[[x1 y1] [x2 y2] [x3 y3]] color]
  (set-color! color)
  (q/triangle x1 y1 x2 y2 x3 y3))

(defn- setup [sketch-atom]
  (let [{:keys [bg-color fg-color]} @sketch-atom]
    (println "Setting up sketch")
    (println "Background color:" bg-color)
    (println "Foreground color:" fg-color)
    (clear! bg-color)
    (set-color! fg-color)
    (q/stroke-cap :square)
    (q/frame-rate 1)))

(defn- draw! [sketch-atom code-atom]
  (clear! (:bg-color @sketch-atom))
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
        (draw-rectangle! position width height color))

      :text
      (let [{:keys [text position size]} form]
        (draw-text! text position size color))

      :triangle
      (let [{:keys [position]} form]
        (draw-triangle! position color))

      nil)))

;; https://github.com/simon-katz/nomisdraw/blob/for-quil-api-request/src/cljs/nomisdraw/utils/nomis_quil_on_reagent.cljs

(defn sketch
  "Wraps `quil.core/sketch` and plays nicely with Reagent.
  Below, C = the canvas that will host the sketch.
  Differs from `quil.core/sketch` as follows:
  - Creates C (rather than C having to be created separately), and the
   `:host` argument is the id of the canvas that will be created (rather
    than the id of an already-existing canvas).
  - Returns a component that wraps C.
  - The :size argument must be either `nil` or a [width height] vector."
  ;; Thoughts on the canvas id:
  ;; (1) You might think we could create our own unique canvas id.
  ;;     But no -- that would break re-rendering.
  ;; (2) You might think this could be done with a macro that creates the
  ;;     canvas id at compile time.
  ;;     But no -- the same call site can create multiple sketches.
  []
  (let [code-atom (re-frame/subscribe [:code])
        sketch-atom (re-frame/subscribe [:sketch])
        sketch-args {:host (:name @sketch-atom)
                     :size (:size @sketch-atom)
                     :setup (partial setup sketch-atom)
                     :draw (partial draw! sketch-atom code-atom)}
        size (:size sketch-args)
        _ (assert (or (nil? size)
                      (and (vector? size)
                           (= (count size) 2)))
                  (str ":size should be nil or a vector of size 2, but it is "
                       size))
        [w h] size
        canvas-id (do
                    (assert (contains? sketch-args :host))
                    (:host sketch-args))
        canvas-tag-&-id (keyword (str "canvas#" canvas-id))]
    [r/create-class
     {:reagent-render
      (fn []
        [canvas-tag-&-id {:style {:max-width w
                                  :max-height h} ; prevent stretching when used in flex container
                          :width  w
                          :height h}])

      :component-did-mount
      (fn []
        ;; Use a go block so that the canvas exists
        ;; before we attach the sketch to it.
        ;; (Needed on initial render; not on
        ;; re-render.)
        (a/go
          (println "Attaching sketch")
          (apply q/sketch
                 (apply concat sketch-args))))

      :component-will-unmount
      (fn []
        (-> canvas-id
            dom/getElement
            q.sketch/destroy-previous-sketch))}]))
