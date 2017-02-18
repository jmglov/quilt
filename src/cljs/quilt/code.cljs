(ns quilt.code
  (:require [cljs.reader :refer [read-string]]
            [clojure.string :as string]
            [quilt.color :as color]
            [quilt.i18n :as i18n])
  (:refer-clojure :exclude [replace]))

(def ^:private color-docstring
  "color as a keyword or vector of [red green blue]")

(def functions
  {:circle {:defaults {:position [0 0]
                       :radius 0}
            :doc "creates a circle around a central point"
            :params [[:position "centre point of as a vector of [x y]"]
                     [:radius "radius as a number"]
                     [:color color-docstring]]}
   :curve {:defaults {:position [0 0]
                      :radius 0
                      :orientation :down
                      :thickness 1}
           :doc "creates a circular curve around a central point"
           :params [[:position "centre point as vector of [x y"]
                    [:radius "radius as a number"]
                    [:orientation "one of :up, :down, :left, or :right"]
                    [:thickness "thickness as a number"]
                    [:color color-docstring]]}
   :line {:defaults {:position [[0 0] [0 0]]
                     :thickness 1}
          :doc "creates a line from a starting point to an ending point"
          :params [[:position "starting and ending points as vector of [[x1 y1] [x2 y2]"]
                   [:thickness "thickness as a number"]
                   [:color color-docstring]]}
   :rectangle {:defaults {:position [0 0]
                          :width 0
                          :height 0}
               :doc "creates a rectangle from an upper left corner point"
               :params [[:position "upper left corner as a vector of [x y]"]
                        [:width "width as a number"]
                        [:height "height as a number"]
                        [:color color-docstring]]}
   :text {:defaults {:position [0 0]
                     :text ""
                     :size 24}
          :doc "creates some text at a top center point"
          :params [[:position "top centre as a vector of [x y]"]
                   [:text "text to display"]
                   [:size "text size as a number"]
                   [:color color-docstring]]}
   :triangle {:defaults {:position [[0 0] [0 0] [0 0]]}
              :doc "creates a triangle with three corner points"
              :params [[:position "three points as vector of [[x1 y1] [x2 y2] [x3 y3]]"]
                       [:color color-docstring]]}})

(def orientations [:down
                   :up
                   :left
                   :right])

(defn add-form [code form]
  (let [form (assoc form :index (count code))
        code (conj code form)]
    code))

(defn add-forms [code forms]
  (reduce add-form code forms))

(defn delete [code {:keys [index] :as form}]
  (let [code (->> code
                  (remove #(= index (:index %)))
                  (into []))]
    code))

(defn replace [code {:keys [index] :as form}]
  (let [old-form (get code index)
        code (assoc code index form)]
    code))

(defn reorder [code cur-index new-index]
  (let [form (nth code cur-index)
        others (remove #(= cur-index (:index %)) code)
        before (take new-index others)
        after (drop new-index others)]
    (add-forms []
               (concat before
                       [(assoc form :index new-index)]
                       after))))

(defn create-form [fun]
  (merge {:fun fun
          :color color/default}
         (get-in functions [fun :defaults])))

(defn- form->str [{:keys [fun] :as form}]
  form
  (str "(" (name fun) " "
       (->> (get-in functions [fun :params])
            (map #(pr-str (form (first %))))
            (string/join " "))
       ")"))

(defn forms->str [forms]
  (->> forms
       (map form->str)
       (string/join "\n")))

(defn- read-line [line]
  (try
    (when-let [[fun & args] (read-string line)]
      (let [fun (keyword fun)
            params (map first (get-in functions [fun :params]))]
        (->> (interleave params args)
             (apply hash-map)
             (merge {:fun fun}))))
    (catch :default e
      (js/alert (str "Error evaluating line: " line))
      (throw (js/Error. :read-line)))))

(defn read [source]
  (try
    (let [code (->> (string/split-lines source)
                    (map read-line)
                    (remove nil?)
                    (into []))]
      code)
    (catch :default _
      nil)))

(defn set-index [form index]
  (assoc form :index index))

(defn last-index [forms]
  (:index (last forms)))

(defn docstring [fun param]
  (if param
    (->> (get-in functions [fun :params])
         (some (fn [[p doc]] (when (= param p) doc)))
         i18n/str
         (str (name param) ": "))
    (str (name fun) ": " (i18n/str (get-in functions [fun :doc])))))
