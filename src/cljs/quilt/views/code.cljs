(ns quilt.views.code
  (:require [cljs.reader :refer [read-string]]
            [quilt.color :refer [color]]
            [quilt.util :refer [concatv get-value]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- replace-code [form]
  (rf/dispatch [:replace-code form]))

(defn- input-assoc [size form path tx default]
  [:input {:type "text"
           :size size
           :maxLength size
           :value (str (get-in form path))
           :on-change #(let [new-value (get-value % tx default)]
                         (replace-code (assoc-in form path new-value)))}])

(defn- input-num [size form path]
  (input-assoc size form path read-string 0))

(defn- input-text [size form path]
  (input-assoc size form path identity ""))

(defn- color-picker [form]
  (concatv
   [:select
    {:value (str (:color form))
     :on-change #(let [new-color (read-string (get-value %))]
                   (replace-code (assoc form :color new-color)))}]
   (map (fn [c] [:option (str c)])
        (keys color))))

(defn- render-circle [form]
  [:div
   "(circle "
   "[" (input-num 3 form [:position 0]) " "
   (input-num 3 form [:position 1]) "] "
   (input-num 3 form [:radius]) ")"])

(defn- render-color [form]
  [:div
   "(color "
   (color-picker form)
   ")"])

(defn- render-text [form]
  [:div
   "(text "
   (input-text 64 form [:text]) " "
   "[" (input-num 3 form [:position 0]) " "
   (input-num 3 form [:position 1]) "] "
   (input-num 2 form [:size]) ")"])

(defn render [{:keys [fun] :as form}]
  (case fun
    :circle (render-circle form)
    :clear [:div "(clear)"]
    :color (render-color form)
    :text (render-text form)
    [:div (pr-str form)]))
