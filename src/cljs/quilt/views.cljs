(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [quil.middleware :as q.middleware]
            [quilt.sketch :refer [sketch]]
            [re-frame.core :as re-frame]))

(defn- quil-setup []
  {})

(defn- quil-update [state]
  state)

(defn- quil-draw! [state])

(defn code-pane []
  (let [code (re-frame/subscribe [:code])]
    (fn []
      [:div (with-out-str (pprint @code))])))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div
       [sketch
        :host "sketch"
        :size [640 480]
        :setup quil-setup
        :update quil-update
        :draw quil-draw!
        :middleware [q.middleware/fun-mode]]
       [code-pane]])))
