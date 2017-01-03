(ns quilt.views.code.reorder
  (:require [goog.events :as events]
            [quilt.util :as util]
            [re-frame.core :as rf])
  (:import (goog.events EventType)))

(def handlers-installed? (atom false))

(defn- forms-element []
  (util/get-element "forms"))

(defn- form-elements []
  (util/children (forms-element)))

(defn- form-position [form-elem]
  (util/relative-position form-elem (forms-element)))

(defn- reorder-code [mouse-event editor-atom]
  (let [[_ mouse-y] (util/relative-position mouse-event (forms-element))
        form-elems (form-elements)
        selected-index (:selected-index @editor-atom)
        before? (fn [i]
                  (let [elem (nth form-elems i)
                        [_ y] (form-position elem)]
                    (println "Form at index" i "y position:" y)
                    (when (and (not= i selected-index)
                               (<= mouse-y y))
                      (if (<= i selected-index) i (dec i)))))
        new-index (or (->> (range 0 (count form-elems))
                           (some before?))
                      (dec (count form-elems)))]
    (when (not= selected-index new-index)
      (println "Moving form from index" selected-index "to" new-index)
      (rf/dispatch [:reorder-code selected-index new-index])
      (rf/dispatch [:select-form new-index]))))

(defn- mouse-move-handler [editor-atom]
  (fn [event]
    (when (:selected-index @editor-atom)
      (reorder-code event editor-atom))))

(defn- mouse-up-handler [editor-atom]
  (fn [event]
    (when (:selected-index @editor-atom)
      (reorder-code event editor-atom)
      (rf/dispatch [:unselect-form]))))

(defn mouse-down-handler [{:keys [fun index] :as form} editor-atom]
  (fn [event]
    (let [handle (.-target event)
          [_ cur-y] (util/relative-position (nth (form-elements) index)
                                            (forms-element))]
      (rf/dispatch [:select-form index])
      (println (str "Dragging " fun " (index " index ") from y position " cur-y)))
    (when-not @handlers-installed?
      (events/listen js/window
                     EventType.MOUSEMOVE
                     (mouse-move-handler editor-atom))
      (events/listen js/window
                     EventType.MOUSEUP
                     (mouse-up-handler editor-atom))
      (reset! handlers-installed? true))))
