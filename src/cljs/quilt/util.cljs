(ns quilt.util)

(defn concatv [& vs]
  (into [] (apply concat vs)))

(defn get-value
  ([element]
   (-> element .-target .-value))
  ([element tx default]
   (-> element get-value tx (or default))))

(defn client-rect
  "Returns the position of the parent element as a vector of [x y], or
    [nil nil] if there is no element or parent element"
  [elem]
  (when elem
    (when-let [r (.getBoundingClientRect elem)]
      [(.-left r) (.-top r)])))

(defn relative-position
  "Returns the position of the element relative to its parent element as a
   vector of [x y]"
  [elem]
  (when elem
    (when-let [[left top] (client-rect elem)]
      [(- (.-clientX elem) left)
       (- (.-clientY elem) top)])))

(defn mouse-position
  "Returns the position of the mouse from the event as a vector of [x y]"
  [event]
  [(.-clientX event) (.-clientY event)])
