(ns quilt.views.code
  (:require [cljs.reader :refer [read-string]]
            [goog.events :as events]
            [quilt.code :as code]
            [quilt.color :as color]
            [quilt.util :as util]
            [quilt.views.widgets :as widgets]
            [re-frame.core :as rf]
            [reagent.core :as r])
  (:import (goog.events EventType)))

;; Make HTMLCollection a Clojure collection
(extend-type js/HTMLCollection
  ICounted
  (-count [coll] (. coll -length))

  IIndexed
  (-nth
    ([coll n] (. coll (item n)))
    ([coll n not-found] (if (<= (. coll -length) n)
                          not-found
                          (nth coll n))))

  ISeqable
  (-seq [coll] (array-seq coll 0)))

(def dragging-form (atom nil))
(def dragging-listener-installed? (atom false))

(defn- on-mouse-up [event]
  (when-let [{:keys [form elem siblings]} @dragging-form]
    (let [parent-pos (util/parent-position elem)
          [_ y] (util/relative-position (util/mouse-position event)
                                        parent-pos)
          ;; The mouse pointer takes up a little room
          y (- y 10)
          _ (println "Dragging" (:fun form) "index" (:index form) "from y"
                     (second (util/relative-position elem)) "to y" y)
          new-index (->> (range 0 (count siblings))
                         (some (fn [i]
                                 (let [sibling (nth siblings i)
                                       [_ sibling-y] (util/relative-position sibling)]
                                   (println "Form y" y "sibling" i "y:" sibling-y)
                                   (when (and (not (identical? elem sibling))
                                              (<= y sibling-y))
                                     (if (<= i (:index form)) i (dec i)))))))]
      (rf/dispatch [:reorder-code form (or new-index (dec (count siblings)))]))
    (reset! dragging-form nil)))

(defn- mouse-down-handler [form]
  (fn [event]
    (let [elem (.-target event)
          [_ y] (util/relative-position (util/mouse-position event) elem)]
      (reset! dragging-form {:form form
                             :elem elem
                             :siblings (-> elem .-parentNode .-children)})
      (println "Dragging from y:" y))
    (when-not @dragging-listener-installed?
      (events/listen js/window EventType.MOUSEUP on-mouse-up)
      (reset! dragging-listener-installed? true))))

(defn- delete-code [form]
  (rf/dispatch [:delete-code form]))

(defn- replace-code [form]
  (rf/dispatch [:replace-code form]))

(defn- input-assoc [size form path tx default]
  (widgets/input #(replace-code (assoc-in form path %))
                 size (str (get-in form path)) tx default))

(defn- input-num [size form path]
  (input-assoc size form path read-string 0))

(defn- input-text [size form path]
  (input-assoc size form path identity ""))

(defn- color-picker [form]
  (util/concatv
   [:select
    {:value (str (:color form))
     :on-change #(let [new-color (read-string (util/get-value %))]
                   (replace-code (assoc form :color new-color)))}]
   (map (fn [c] [:option (str c)])
        color/basic)))

(defn- orientation-picker [form]
  (util/concatv
   [:select
    {:value (str (:orientation form))
     :on-change #(let [new-value (read-string (util/get-value %))]
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

(defn- render-line [form]
  ["(line "
   "[[" (input-num 3 form [:position 0 0]) " "
   (input-num 3 form [:position 0 1]) "] ["
   (input-num 3 form [:position 1 0]) " "
   (input-num 3 form [:position 1 1]) "]] "
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

(defn- render-triangle [form]
  ["(triangle "
   "[[" (input-num 3 form [:position 0 0]) " "
   (input-num 3 form [:position 0 1]) "] ["
   (input-num 3 form [:position 1 0]) " "
   (input-num 3 form [:position 1 1]) "] ["
   (input-num 3 form [:position 2 0]) " "
   (input-num 3 form [:position 2 1]) "]] "
   (color-picker form) ")"])

(defn render [{:keys [fun] :as form}]
  (util/concatv [:div.form {:on-mouse-down (mouse-down-handler form)}]
                (case fun
                  :circle (render-circle form)
                  :curve (render-curve form)
                  :line (render-line form)
                  :rectangle (render-rectangle form)
                  :text (render-text form)
                  :triangle (render-triangle form))
                [[:button {:on-click #(delete-code form)} "Delete"]]))
