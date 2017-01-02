(ns quilt.util)

(defn concatv [& vs]
  (into [] (apply concat vs)))

(defn add-class [elem css-class]
  (-> elem .-classList (.add css-class)))

(defn remove-class [elem css-class]
  (-> elem .-classList (.remove css-class)))

(defn ancestor
  "Returns the nth ancestor of elem"
  [elem n]
  (when (and elem (pos-int? n))
    (loop [n n
           elem elem]
      (if (or (= 0 n) (nil? elem))
        elem
        (recur (dec n) (.-parentElement elem))))))

(defn parent [elem]
  (ancestor elem 1))

(defn get-value
  ([element]
   (-> element .-target .-value))
  ([element tx default]
   (-> element get-value tx (or default))))

(defn position
  "Returns the position of the element as a vector of [x y], or
   [nil nil] if there is no element"
  [elem]
  (when elem
    (when-let [r (.getBoundingClientRect elem)]
      [(.-left r) (.-top r)])))

(defn parent-position
  "Returns the position of the element's parent as a vector of [x y], or
   [nil nil] if there is no element or the element has no parent (i.e. is
   the top-level element in the DOM)"
  [elem]
  (and elem (position (.-parentElement elem))))

(defn relative-position
  "Returns the position of an element relative to its parent as a vector of
   [x y], or [nil nil] if either the element or the parent are nil. If two
   elements are specified, returns position of elem1 relative to elem2.
   Elements may be DOM elements or position vectors."
  ([elem]
   (relative-position elem (parent-position elem)))
  ([elem1 elem2]
   (when (and elem1 elem2)
     (let [[x1 y1] (if (vector? elem1) elem1 (position elem1))
           [x2 y2] (if (vector? elem2) elem2 (position elem2))]
       [(- x1 x2) (- y1 y2)]))))

(defn mouse-position
  "Returns the position of the mouse from the event as a vector of [x y]"
  [event]
  [(.-clientX event) (.-clientY event)])
