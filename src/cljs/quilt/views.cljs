(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [clojure.string :as string]
            [quilt.i18n :as i18n]
            [quilt.library :as library]
            [quilt.sketch :refer [sketch]]
            [quilt.util :refer [concatv get-value]]
            [quilt.views.editor :as editor]
            [quilt.views.widgets :as widgets]
            [re-frame.core :as rf]
            [reagent.core :as r]))

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
          [:span.label (i18n/str "Drawing size")]
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
        [:span.label (if (:locked? @mouse-atom)
                       (i18n/str "Saved position")
                       (i18n/str "Current position"))]
        (let [[x y] (:pos @mouse-atom)]
          [:span (str "[" x " " y "]")])]
       [:div (if (:locked? @mouse-atom)
               (i18n/str "Click drawing to show current position")
               (i18n/str "Click drawing to save current position"))]])))

(defn- library []
  (let [sketch-name (r/atom (first (keys library/sketches)))
        set-sketch #(reset! sketch-name (keyword (get-value %)))]
    (fn []
      [:div#library.container
       (str (i18n/str "Load drawing") ":")
       (concatv [:select {:value (name @sketch-name)
                          :on-change set-sketch}]
                (mapv (fn [s] [:option (name s)])
                      (keys library/sketches)))
       [:button {:on-click #(rf/dispatch [:load-sketch @sketch-name])}
        (i18n/str "OK")]])))

(defn- debug []
  (let [db-atom (rf/subscribe [:db])
        editor-atom (rf/subscribe [:editor])
        indentation (fn [s] (count (take-while #(= % \space) s)))]
    (fn []
      (when (:debug? @editor-atom)
        [:div#debug
         [:h2 (i18n/str "Debug")]
         (concatv [:div.outlined]
                  (mapv (fn [line]
                          [:div
                           {:style {:padding-left (str (indentation line) "em")}}
                           line])
                        (->  (pprint @db-atom)
                             with-out-str
                             (string/split "\n"))))]))))

(defn- language []
  (let [lang-atom (rf/subscribe [:language])
        select-lang #(rf/dispatch
                      [:select-lang
                       (i18n/language-code (get-value %))])]
    (fn []
      [:div#lang.container
       [:div (str (i18n/str "Language") ": " (i18n/language-label @lang-atom))]
       ;; TODO: dynamically change language
       #_(concatv [:select {:value (str @lang-atom)
                          :on-change select-lang}]
                (mapv (fn [[code label]]
                        [:option {:value code} label])
                      i18n/languages))])))

(defn main-panel []
  (fn []
    [:div.container
     [:div
      [sketch]
      [:div.container
       [sketch-size]
       [mouse-pos]]
      [language]]
     [editor/editor]
     [library]
     [debug]]))
