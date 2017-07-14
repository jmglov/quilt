(ns quilt.views
  (:require [quilt.views.debug :as debug]
            [quilt.views.editor :as editor]
            [quilt.views.language :as language]
            [quilt.views.library :as library]
            [quilt.views.sketch :as sketch]
            [quilt.views.widgets :as widgets]
            [re-frame.core :as rf]))

(defn main-panel []
  (let [sketch-atom (rf/subscribe [:sketch])]
    (fn []
      [:div#main
       [:div#left-pane
        [sketch/sketch]
        [:div#left-bottom
         [:div#sketch-controls
          [sketch/size]
          [sketch/mouse-pos]]]
        [language/language]]
       [:div#right-pane
        [editor/editor]
        [library/library]
        [debug/debug]]])))
