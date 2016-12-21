(ns quilt.views.code
  (:require [cljs.reader :refer [read-string]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- replace-code [form]
  (rf/dispatch [:replace-code form]))

(defn- input-assoc [size form path tx default]
  (let [get-value #(-> % .-target .-value tx (or default))]
    [:input {:type "text"
             :size size
             :maxLength size
             :value (str (get-in form path))
             :on-change #(let [new-value (get-value %)]
                           (replace-code (assoc-in form path new-value)))}]))

(defn- input-num [size form path]
  (input-assoc size form path read-string 0))

(defn- input-text [size form path]
  (input-assoc size form path identity ""))

(defn- render-color [form]
  [:div
   "(color "
   "[" (input-num 3 form [:color 0]) " "
   (input-num 3 form [:color 1]) " "
   (input-num 3 form [:color 2]) "])"])

(defn- render-text [form]
  (println "Rendering text form:" form)
  [:div
   "(text "
   (input-text 64 form [:text]) " "
   "[" (input-num 3 form [:position 0]) " "
   (input-num 3 form [:position 1]) "] "
   (input-num 2 form [:size]) ")"])

(defn render [{:keys [fun] :as form}]
  (case fun
    :clear [:div "(clear)"]
    :color (render-color form)
    :text (render-text form)
    [:div (pr-str form)]))
