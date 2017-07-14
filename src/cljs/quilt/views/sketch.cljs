(ns quilt.views.sketch
  (:require [clojure.string :as string]
            [quilt.color :refer [->html-color]]
            [quilt.i18n :as i18n]
            [quilt.util :as util :refer [concatv]]
            [quilt.views.widgets :as widgets]
            [re-frame.core :as rf]))

(defn- ->num [n]
  (or n 0))

(defn- ->nums [ns]
  (map ->num ns))

(defn- make-circle [[x y] radius color]
  [:circle {:style {:stroke-width 0}
            :cx x
            :cy y
            :r radius
            :fill (->html-color color)}])

;; http://stackoverflow.com/a/18473154/58994

(defn- polar->cartesian [[x y] radius angle-deg]
  (let [angle-rad (/ (* (- angle-deg 90) Math/PI) 180.0)
        [x y radius] (->nums [x y radius])]
    [(+ x (int (* radius (Math/cos angle-rad))))
     (+ y (int (* radius (Math/sin angle-rad))))]))

(defn- make-curve [pos radius orientation thickness color]
  (let [[radius thickness] (->nums [radius thickness])
        [angle1 angle2] (case orientation
                          :down [270 90]
                          :left [0 180]
                          :right [180 0]
                          :up [90 270])
        [x1 y1] (polar->cartesian pos radius angle1)
        [x2 y2] (polar->cartesian pos radius angle2)
        arc-flag "0"]
    [:path {:style {:stroke (->html-color color)
                    :stroke-width thickness}
            :fill-opacity 0
            :d (->> ["M" x1 y1
                     "A" radius radius 0 arc-flag 0 x2 y2]
                    (string/join " "))}]))

(defn- make-line [[[x1 y1] [x2 y2]] thickness color]
  (let [[x1 y1 x2 y2 thickness] (->nums [x1 y1 x2 y2 thickness])]
    [:line {:style {:stroke-width thickness
                    :stroke (->html-color color)}
            :x1 x1, :y1 y1
            :x2 x2, :y2 y2}]))

(defn- make-rectangle [[x y] width height color]
  (let [[x y width height] (->nums [x y width height])]
    [:rect {:style {:stroke-width 0}
            :x x, :y y
            :width width, :height height
            :fill (->html-color color)}]))

(defn- make-text [text [x y] size color]
  (let [[x y size] (->nums [x y size])]
    [:text {:x x, :y y
            :font-size size
            :fill (->html-color color)
            :text-anchor :middle}
     text]))

(defn- make-triangle
  [position color]
  (let [points (->> position
                    (map #(string/join "," (->num %)))
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

(defn create-shapes [code]
  (mapv ->shape code))

(defn- set-sketch-size [sketch-atom width height]
  (rf/dispatch [:set-sketch-size width height]))

(defn size []
  (let [sketch-atom (rf/subscribe [:sketch])
        simple-ui? (rf/subscribe [:simple-ui?])
        set-size #(set-sketch-size sketch-atom %1 %2)]
    (fn []
      (let [[width height] (:size @sketch-atom)]
        [:div#sketch-size
         [:span.label (i18n/str "Drawing size")]
         (if @simple-ui?
           [:div (str "[" width " " height "]")]
           [:div
            "["
            (widgets/input-num #(set-size % height)
                               3 (str width))
            " "
            (widgets/input-num #(set-size width %)
                               3 (str height))
            "]"])]))))

(defn mouse-pos []
  (let [mouse-atom (rf/subscribe [:mouse])
        sketch-atom (rf/subscribe [:sketch])]
    (fn []
      [:div#mouse-pos
       {:on-click #(rf/dispatch [:lock-mouse-pos])}
       [:div#position
        [:span.label (if (:locked? @mouse-atom)
                       (i18n/str "Saved position")
                       (i18n/str "Current position"))]
        (let [[x y] (:pos @mouse-atom)]
          [:span (str "[" (int x) " " (int y) "]")])]
       [:div (if (:locked? @mouse-atom)
               (i18n/str "Click drawing to show current position")
               (i18n/str "Click drawing to save current position"))]])))

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
         (create-shapes @code-atom))))))
