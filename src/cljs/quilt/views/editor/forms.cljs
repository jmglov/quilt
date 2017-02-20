(ns quilt.views.editor.forms
  (:require [cljs.pprint :refer [pprint]]
            [re-frame.core :as rf]))

(defn editor []
  (let [code-atom (rf/subscribe [:code])
        editor-atom (rf/subscribe [:editor])]
    (fn []
      (when (= :forms (:type @editor-atom))
        [:div#forms-editor.editor
         [:textarea {:readOnly true
                     :value (with-out-str (pprint @code-atom))}]]))))
