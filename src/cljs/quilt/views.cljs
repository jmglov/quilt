(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [quilt.sketch :as sketch :refer [sketch]]
            [re-frame.core :as re-frame]))

(defn code-pane []
  (let [code (re-frame/subscribe [:code])]
    (fn []
      [:div (with-out-str (pprint @code))])))

(defn main-panel []
  (fn []
    [:div
     [sketch]
     [code-pane]]))
