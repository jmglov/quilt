(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [cljs.reader :refer [read-string]]
            [clojure.string :as string]
            [hiccups.runtime]
            [quilt.code :as code]
            [quilt.library :as library]
            [quilt.sketch :as sketch :refer [sketch]]
            [quilt.util :refer [concatv consv get-value]]
            [quilt.views.code :as views.code]
            [quilt.views.widgets :as widgets]
            [re-frame.core :as rf]
            [reagent.core :as r])
  (:require-macros [hiccups.core :as hiccups]))

(defn- clear-code []
  (rf/dispatch [:clear-code]))

(defn- set-sketch-size [sketch-atom width height]
  (rf/dispatch [:set-sketch-size width height]))

(defn- sketch-size []
  (let [sketch-atom (rf/subscribe [:sketch])
        simple-ui? (rf/subscribe [:simple-ui?])
        set-size #(set-sketch-size sketch-atom %1 %2)]
    (fn []
      (let [[width height] (:size @sketch-atom)]
        [:div#sketch-options
         [:div.container
          [:span.label "Drawing size"]
          (if @simple-ui?
            [:div (str "[" width " " height "]")]
            [:div
             "["
             (widgets/input-num #(set-size % height)
                                3 (str width))
             " "
             (widgets/input-num #(set-size width %)
                                3 (str height))
             "]"])]]))))

(defn- mouse-pos []
  (let [mouse-atom (rf/subscribe [:mouse])
        sketch-atom (rf/subscribe [:sketch])]
    (fn []
      [:div#mouse-pos
       {:on-click #(rf/dispatch [:lock-mouse-pos])}
       [:div
        [:span.label "Current position"]
        (let [[x y] (:pos @mouse-atom)]
          [:span (str "[" x " " y "]")])]
       [:div (if (:locked? @mouse-atom)
               "Click drawing to show moving"
               "Click drawing to remember")]])))

(defn- visual-editor []
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
            (map (fn [[fun _]] [:option (name fun)]) code/functions))
           [:button {:on-click add-code} "Add"]]
          [:button {:on-click clear-code} "Delete all"]]]))))

(defn- source-editor []
  (let [code-atom (rf/subscribe [:code])
        editor-atom (rf/subscribe [:editor])
        source-atom (rf/subscribe [:source])
        eval-code #(rf/dispatch [:eval-code])]
    (fn []
      (when (= :source (:type @editor-atom))
        [:div#source-editor.editor
         [:textarea {:readOnly (:readonly? @editor-atom)
                     :value @source-atom
                     :on-change #(rf/dispatch [:set-source (get-value %)])}]
         [:div.container
          [:button {:on-click eval-code} "Eval"]
          [:button {:on-click #(rf/dispatch [:set-source ""])}
           "Clear"]]]))))

(defn- forms-editor []
  (let [code-atom (rf/subscribe [:code])
        editor-atom (rf/subscribe [:editor])]
    (fn []
      (when (= :forms (:type @editor-atom))
        [:div#forms-editor.editor
         [:textarea {:readOnly true
                     :value (with-out-str (pprint @code-atom))}]]))))

(defn- html-editor []
  (let [code-atom (rf/subscribe [:code])
        editor-atom (rf/subscribe [:editor])]
    (fn []
      (when (= :html (:type @editor-atom))
        (let [shapes (consv :svg (sketch/create-shapes @code-atom))]
          [:div#html-editor.editor
           [:textarea {:readOnly true
                       :value (hiccups/html shapes)}]])))))

(defn- editor-options []
  (let [editor-atom (rf/subscribe [:editor])
        select-editor #(rf/dispatch
                        [:select-editor
                         (keyword (string/lower-case (get-value %)))])
        simple-ui? (rf/subscribe [:simple-ui?])]
    (fn []
      [:div#editor-options.container
       [:div#editor-type
        "Editor:"
        [:select {:value (let [type (:type @editor-atom)]
                           (if (= :html type)
                             "HTML"
                             (string/capitalize (name type))))
                  :on-change select-editor}
         [:option "Visual"]
         [:option "Source"]
         [:option "HTML"]
         [:option "Forms"]]]
       [:div#readonly-toggle
        {:style (if @simple-ui? {:display "none"} {})}
        [:input.editor-options-checkbox
         {:type "checkbox"
          :checked (:readonly? @editor-atom)
          :on-change #(rf/dispatch [:toggle-readonly])}]
        "Read only?"]
       [:div#debug-toggle
        {:style (if @simple-ui? {:display "none"} {})}
        [:input.editor-options-checkbox
         {:type "checkbox"
          :checked (:debug? @editor-atom)
          :on-change #(rf/dispatch [:toggle-debug])}]
        "Show debug?"]])))

(defn- library []
  (let [sketch-name (r/atom (first (keys library/sketches)))
        set-sketch #(reset! sketch-name (keyword (get-value %)))]
    (fn []
      [:div#library.container
       "Load drawing:"
       (concatv [:select {:value (name @sketch-name)
                          :on-change set-sketch}]
                (mapv (fn [s] [:option (name s)])
                      (keys library/sketches)))
       [:button {:on-click #(rf/dispatch [:load-sketch @sketch-name])}
        "OK"]])))

(defn- debug []
  (let [db-atom (rf/subscribe [:db])
        editor-atom (rf/subscribe [:editor])
        indentation (fn [s] (count (take-while #(= % \space) s)))]
    (fn []
      (when (:debug? @editor-atom)
        [:div#debug
         [:h2 "Debug"]
         (concatv [:div.outlined]
                  (mapv (fn [line]
                          [:div
                           {:style {:padding-left (str (indentation line) "em")}}
                           line])
                        (->  (pprint @db-atom)
                             with-out-str
                             (string/split "\n"))))]))))

(defn- language []
  (let [lang-atom (rf/subscribe [:language])]
    (fn []
      [:div "Language: " @lang-atom])))

(defn main-panel []
  (fn []
    [:div.container
     [:div
      [sketch]
      [:div.container
       [sketch-size]
       [mouse-pos]]
      [language]]
     [:div#editor
      [:h2 "Code"]
      [visual-editor]
      [source-editor]
      [forms-editor]
      [html-editor]
      [editor-options]
      [library]
      [debug]]]))
