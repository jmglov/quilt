(ns quilt.views
  (:require [quilt.util :refer [concatv]]
            [quilt.views.debug :as debug]
            [quilt.views.editor :as editor]
            [quilt.views.language :as language]
            [quilt.views.library :as library]
            [quilt.views.sketch :as sketch]
            [quilt.views.widgets :as widgets]
            [re-frame.core :as rf]))

(defn- left-pane-styles [{[width _] :size}]
  {:width (str (+ width 15) "px")})

(defn main-panel []
  (let [sketch-atom (rf/subscribe [:sketch])]
    (fn []
      (concatv
       [:div.container
        [:div#left-pane {:style (left-pane-styles @sketch-atom)}
         [sketch/sketch]
         [:div.container
          [sketch/size]
          [sketch/mouse-pos]]
         [language/language]]
        [:div#right-pane
         [editor/editor]
         [library/library]
         [debug/debug]]]))))
