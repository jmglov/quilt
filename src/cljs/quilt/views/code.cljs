(ns quilt.views.code
  (:require [cljs.reader :refer [read-string]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- replace-code [form]
  (rf/dispatch [:replace-code form]))

(defn- input-assoc [size form path]
  (let [get-value (fnil #(-> % .-target .-value read-string) 0)]
    [:input {:type "text"
             :size (str size)
             :maxLength (str size)
             :value (str (or (get-in form path) 0))
             :on-change #(let [new-form (assoc-in form path (get-value %))]
                           (println path "changed:" (get-value %))
                           (println "New val:" form)
                           (replace-code new-form))}]))

(defn- render-color [form]
  (println "Rendering color form:" form)
  [:div
   "(color "
   (input-assoc 3 form [:color 0]) " "
   (input-assoc 3 form [:color 1]) " "
   (input-assoc 3 form [:color 2]) ")"])

(defn render [{:keys [fun] :as form}]
  (case fun
    :clear [:div "(clear)"]
    :color (render-color form)
    [:div (pr-str form)]))
