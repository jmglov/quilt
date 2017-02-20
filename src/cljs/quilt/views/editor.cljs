(ns quilt.views.editor
  (:require [clojure.string :as string]
            [quilt.i18n :as i18n]
            [quilt.views.editor.forms :as forms]
            [quilt.views.editor.html :as html]
            [quilt.views.editor.source :as source]
            [quilt.views.editor.visual :as visual]
            [quilt.util :refer [get-value]]
            [re-frame.core :as rf]))

(defn options []
  (let [editor-atom (rf/subscribe [:editor])
        select-editor #(rf/dispatch
                        [:select-editor
                         (keyword (string/lower-case (get-value %)))])
        simple-ui? (rf/subscribe [:simple-ui?])]
    (fn []
      [:div#editor-options.container
       [:div#editor-type
        (str (i18n/str "Editor") ":")
        [:select {:value (let [type (:type @editor-atom)]
                           (if (= :html type)
                             "HTML"
                             (string/capitalize (name type))))
                  :on-change select-editor}
         [:option {:value "Visual"} (i18n/str "Visual")]
         [:option {:value "Source"} (i18n/str "Source")]
         [:option {:value "HTML"} (i18n/str "HTML")]
         [:option {:value "Forms"} (i18n/str "Forms")]]]
       [:div#readonly-toggle
        {:style (if @simple-ui? {:display "none"} {})}
        [:input.editor-options-checkbox
         {:type "checkbox"
          :checked (:readonly? @editor-atom)
          :on-change #(rf/dispatch [:toggle-readonly])}]
        (i18n/str "Read only?")]
       [:div#debug-toggle
        {:style (if @simple-ui? {:display "none"} {})}
        [:input.editor-options-checkbox
         {:type "checkbox"
          :checked (:debug? @editor-atom)
          :on-change #(rf/dispatch [:toggle-debug])}]
        (i18n/str "Show debug?")]])))

(defn editor []
  (fn []
    [:div#editor
     [:h2 (i18n/str "Code")]
     [visual/editor]
     [source/editor]
     [forms/editor]
     [html/editor]
     [options]]))
