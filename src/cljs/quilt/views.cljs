(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [re-frame.core :as re-frame]))

(defn sketch-pane []
  (fn []
    [:canvas#sketch]))

(defn code-pane []
  (let [code (re-frame/subscribe [:code])]
    (fn []
      [:div (with-out-str (pprint @code))])))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div
       [sketch-pane]
       [code-pane]])))
