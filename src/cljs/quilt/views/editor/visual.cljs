(ns quilt.views.editor.visual
  (:require [quilt.code :as code]
            [quilt.i18n :as i18n]
            [quilt.util :refer [concatv get-value]]
            [quilt.views.code :as views.code]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn editor []
  (let [code-atom (rf/subscribe [:code])
        editor-atom (rf/subscribe [:editor])
        sketch-atom (rf/subscribe [:sketch])
        new-fun (r/atom (key (first code/functions)))
        add-code #(do
                    (rf/dispatch [:add-code (code/create-form @new-fun)])
                    (rf/dispatch [:show-docstring-new-form]))]
    (fn []
      (when (= :visual (:type @editor-atom))
        [:div#visual-editor
         (concatv [:div#forms.outlined]
                  (map #(views.code/render % editor-atom @sketch-atom)
                       @code-atom))
         [:div#modify-forms.container
          [:div.outlined
           (concatv
            [:select
             {:value (name @new-fun)
              :on-change #(reset! new-fun (keyword (get-value %)))}]
            (map (fn [[fun _]] [:option (name fun)])
                 code/functions))
           [:button {:on-click add-code} (i18n/str "Add")]]
          [:button#clear-code {:on-click clear-code} (i18n/str "Delete all")]]]))))
