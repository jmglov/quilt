(ns quilt.views.code.reorder
  (:require [goog.events :as events]
            [quilt.util :as util]
            [re-frame.core :as rf])
  (:import (goog.events EventType)))

(def ^:private selected-form (atom nil))
(def handler-installed? (atom false))

(defn- forms-element []
  (util/get-element "forms"))

(defn- form-elements []
  (util/children (forms-element)))

(defn- form-position [form-elem]
  (util/relative-position form-elem (forms-element)))

(defn- form-for [handle-elem]
  (util/parent handle-elem))

(defn- select-form [form-elem]
  (util/add-class form-elem "selected-form"))

(defn- unselect-form [form-elem]
  (util/remove-class form-elem "selected-form"))

(defn index-before [selected-elem y index form-elems i]
  (let [cur-elem (nth form-elems i)
        [_ cur-y] (form-position cur-elem)
        cur-y (quot (.-clientHeight cur-elem) 2)]
    (println "Form at index" i "y position:" cur-y)
    (when (and (not (identical? selected-elem cur-elem))
               (<= y cur-y))
      (if (<= i index) i (dec i)))))

(defn- on-mouse-up [event]
  (when-let [{:keys [form selected-elem]} @selected-form]
    (let [{:keys [fun index]} form
          [_ mouse-y] (util/relative-position event (forms-element))
          _ (println "Dropping at" mouse-y)
          form-elems (form-elements)
          _ (println "Form elems" form-elems)
          before-form (partial index-before
                               selected-elem mouse-y index form-elems)
          new-index (->> (range 0 (count form-elems))
                         (some before-form))]
      (println "Moving" fun "from index" index "to" new-index)
      (unselect-form selected-elem)
      (rf/dispatch [:reorder-code form (or new-index
                                           (dec (count form-elems)))]))
    (reset! selected-form nil)))

(defn- mouse-down-handler [{:keys [fun index] :as form}]
  (fn [event]
    (let [handle (.-target event)
          form-elem (form-for handle)
          [_ cur-y] (util/relative-position form-elem (forms-element))]
      (select-form form-elem)
      (reset! selected-form {:form form, :selected-elem form-elem})
      (println (str "Dragging " fun " (index " index ") from y position " cur-y)))
    (when-not @handler-installed?
      (events/listen js/window EventType.MOUSEUP on-mouse-up)
      (reset! handler-installed? true))))
