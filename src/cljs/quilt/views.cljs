(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [clojure.string :as string]
            [quilt.code :as code]
            [quilt.library :as library]
            [quilt.sketch :as sketch :refer [sketch]]
            [quilt.util :refer [concatv get-value]]
            [quilt.views.code :as views.code]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- clear-code []
  (rf/dispatch [:clear-code]))

(defn- visual-editor []
  (let [code-atom (rf/subscribe [:code])
        editor-atom (rf/subscribe [:editor])
        new-fun (r/atom (key (first code/functions)))
        add-code #(rf/dispatch [:add-code (code/create-form @new-fun)])]
    (fn []
      (when (= :visual (:type @editor-atom))
        [:div
         (concatv [:div#forms.outlined]
                  (mapv views.code/render @code-atom))
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
        [:div#source-editor
         [:textarea {:value @source-atom
                     :on-change #(rf/dispatch [:set-source (get-value %)])}]
         [:div.container
          [:button {:on-click eval-code} "Eval"]
          [:button {:on-click #(rf/dispatch [:set-source ""])}
           "Clear"]]]))))

(defn- editor-options []
  (let [editor-atom (rf/subscribe [:editor])
        select-editor #(rf/dispatch
                        [:select-editor
                         (keyword (string/lower-case (get-value %)))])]
    (fn []
      [:div#editor-options.container
       [:div#editor-type
        "Editor:"
        [:select {:value (string/capitalize (name (:type @editor-atom)))
                  :on-change select-editor}
         [:option "Visual"]
         [:option "Source"]]]
       [:div#debug-toggle
        [:input {:type "checkbox"
                 :checked (:debug? @editor-atom)
                 :on-change #(rf/dispatch [:toggle-debug])}]
        "Show debug?"]])))

(defn- library []
  (let [sketch-name (r/atom (first (keys library/sketches)))
        set-sketch #(reset! sketch-name (keyword (get-value %)))]
    (println (keys library/sketches))
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

(defn main-panel []
  (fn []
    [:div.container
     [sketch]
     [:div#editor
      [:h2 "Code"]
      [visual-editor]
      [source-editor]
      [editor-options]
      [library]
      [debug]]]))
