(ns quilt.views.code
  (:require [cljs.reader :refer [read-string]]
            [quilt.code :as code]
            [quilt.color :refer [color]]
            [quilt.util :refer [concatv get-value]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- delete-code [form]
  (rf/dispatch [:delete-code form]))

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

(defn- orientation-picker [form]
  (concatv
   [:select
    {:value (str (:orientation form))
     :on-change #(let [new-value (read-string (get-value %))]
                   (replace-code (assoc form :orientation new-value)))}]
   (map (fn [c] [:option (str c)])
        code/orientations)))

(defn- render-circle [form]
  ["(circle "
   "[" (input-num 3 form [:position 0]) " "
   (input-num 3 form [:position 1]) "] "
   (input-num 3 form [:radius]) " "
   (color-picker form) ")"])

(defn- render-curve [form]
  ["(curve "
   "[[" (input-num 3 form [:position 0 0]) " "
   (input-num 3 form [:position 0 1]) "] ["
   (input-num 3 form [:position 1 0]) " "
   (input-num 3 form [:position 1 1]) "]] "
   (orientation-picker form) " "
   (input-num 2 form [:thickness]) " "
   (color-picker form) ")"])

(defn- render-rectangle [form]
  ["(rectangle "
   "[" (input-num 3 form [:position 0]) " "
   (input-num 3 form [:position 1]) "] "
   (input-num 3 form [:width]) " "
   (input-num 3 form [:height]) " "
   (color-picker form) ")"])

(defn- render-text [form]
  ["(text "
   "[" (input-num 3 form [:position 0]) " "
   (input-num 3 form [:position 1]) "] "
   (input-text 30 form [:text]) " "
   (input-num 2 form [:size]) " "
   (color-picker form) ")"])

(defn render [{:keys [fun] :as form}]
  (concatv [:div.form]
           (case fun
             :circle (render-circle form)
             :curve (render-curve form)
             :rectangle (render-rectangle form)
             :text (render-text form))
           [[:button {:on-click #(delete-code form)} "Delete"]]))
