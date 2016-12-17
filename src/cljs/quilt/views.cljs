(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [quilt.sketch :as sketch :refer [sketch]]
            [re-frame.core :as re-frame]))

(defn sketch-pane []
  [sketch
   :host "sketch"
   :size [640 480]
   :setup sketch/setup
   :draw sketch/draw!])

(defn code-pane []
  (let [code (re-frame/subscribe [:code])]
    (fn []
      [:div (with-out-str (pprint @code))])))

(defn main-panel []
  (fn []
    [:div
     [sketch-pane]
     [code-pane]]))
