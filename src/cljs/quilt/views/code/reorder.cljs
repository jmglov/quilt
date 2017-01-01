(ns quilt.views.code.reorder
  (:require [goog.events :as events]
            [quilt.util :as util]
            [re-frame.core :as rf])
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

(def ^:private selected-form (atom nil))
(def handler-installed? (atom false))

(defn- forms-elem [handle]
  (util/ancestor handle 2))

(defn- form-position [handle]
  (util/relative-position handle (forms-elem handle)))

(defn- siblings [form-elem]
  (.-children (util/parent form-elem)))

(defn index-before [selected-elem y index form-elems i]
  (let [cur-elem (nth form-elems i)
        [_ cur-y] (util/relative-position cur-elem)
        cur-y (quot (.-clientHeight cur-elem) 2)]
    (println "Form at index" i "y position:" cur-y)
    (when (and (not (identical? selected-elem cur-elem))
               (<= y cur-y))
      (if (<= i index) i (dec i)))))

(defn- on-mouse-up [event]
  (when-let [{:keys [form selected-elem]} @selected-form]
    (let [{:keys [fun index]} form
          [_ mouse-y] (util/relative-position (util/mouse-position event)
                                              (util/parent selected-elem))
          _ (println "Dropping at" mouse-y)
          form-elems (siblings selected-elem)
          _ (println "Form elems" form-elems)
          before-form (partial index-before
                               selected-elem mouse-y index form-elems)
          new-index (->> (range 0 (count form-elems))
                         (some before-form))]
      (println "Moving" fun "from index" index "to" new-index)
      (rf/dispatch [:reorder-code form (or new-index
                                           (dec (count form-elems)))]))
    (reset! selected-form nil)))

(defn- mouse-down-handler [{:keys [fun index] :as form}]
  (fn [event]
    (let [handle (.-target event)
          [_ y] (form-position handle)]
      (reset! selected-form {:form form, :selected-elem (util/parent handle)})
      (println (str "Dragging " fun " (index " index ") from y position " y)))
    (when-not @handler-installed?
      (events/listen js/window EventType.MOUSEUP on-mouse-up)
      (reset! handler-installed? true))))
