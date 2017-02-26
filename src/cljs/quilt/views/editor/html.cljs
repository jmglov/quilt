(ns quilt.views.editor.html
  (:require [hiccups.runtime]
            [quilt.util :refer [consv]]
            [quilt.views.sketch :as sketch]
            [re-frame.core :as rf])
  (:require-macros [hiccups.core :as hiccups]))

(defn editor []
  (let [code-atom (rf/subscribe [:code])
        editor-atom (rf/subscribe [:editor])]
    (fn []
      (when (= :html (:type @editor-atom))
        (let [shapes (consv :svg (sketch/create-shapes @code-atom))]
          [:div#html-editor.editor
           [:textarea {:readOnly true
                       :value (hiccups/html shapes)}]])))))
