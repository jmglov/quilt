(ns quilt.util
  (:require [goog.array :as garray]
            [goog.dom :as gdom]
            [goog.style :as gstyle]))


;; Hiccup
;; -----------------------------------------------------------------------------

(defn concatv [& vs]
  (into [] (apply concat vs)))

(defn consv [x xs]
  (into [] (cons x xs)))

;; Styling
;; -----------------------------------------------------------------------------

(defn add-class [elem css-class]
  (-> elem .-classList (.add css-class)))

(defn remove-class [elem css-class]
  (-> elem .-classList (.remove css-class)))


;; Forms
;; -----------------------------------------------------------------------------

(defn get-value
  ([element]
   (-> element .-target .-value))
  ([element tx default]
   (-> element get-value tx (or default))))


;; DOM
;; -----------------------------------------------------------------------------

(defn children [elem]
  (-> (gdom/getChildren elem)
      garray/toArray))

(defn get-element [id]
  (gdom/getRequiredElement id))

(defn parent [elem]
  (gdom/getParentElement elem))

(defn relative-position
  "Returns the position of one element or mouse event relative to another as a
   vector of [x y], or [nil nil] if either of the elements / events are nil"
  ([e1 e2]
   (when (and e1 e2)
     (let [pos (gstyle/getRelativePosition e1 e2)]
       [(.-x pos) (.-y pos)]))))
