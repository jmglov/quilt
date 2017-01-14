(ns quilt.code
  (:require [cljs.reader :refer [read-string]]
            [clojure.string :as string]
            [quilt.color :as color])
  (:refer-clojure :exclude [replace]))

(def functions
  {:circle {:defaults {:position [0 0]
                       :radius 0}
            :doc "Creates a circle around a central point"
            :params [[:position "Centre point of as a vector of [x y]"]
                     [:radius "Radius as a number"]
                     [:color "Color as a vector of [red green blue]"]]}
   :curve {:defaults {:position [[0 0] [0 0]]
                      :orientation :down
                      :thickness 1}
           :doc "Creates a curve from a starting point to an ending point"
           :params [[:position "Starting and ending points as vector of [[x1 y1] [x2 y2]"]
                    [:orientation "One of :up, :down, :left, or :right"]
                    [:thickness "Thickness as a number"]
                    [:color "Color as a vector of [red green blue]"]]}
   :line {:defaults {:position [[0 0] [0 0]]
                     :thickness 1}
          :doc "Creates a line from a starting point to an ending point"
          :params [[:position "Starting and ending points as vector of [[x1 y1] [x2 y2]"]
                   [:thickness "Thickness as a number"]
                   [:color "Color as a vector of [red green blue]"]]}
   :rectangle {:defaults {:position [0 0]
                          :width 0
                          :height 0}
               :doc "Creates a rectangle from an upper left corner point"
               :params [[:position "Upper left corner as a vector of [x y]"]
                        [:width "Width as a number"]
                        [:height "Height as a number"]
                        [:color "Color as a vector of [red green blue]"]]}
   :text {:defaults {:position [0 0]
                     :text ""
                     :size 24}
          :doc "Creates some text at a top center point"
          :params [[:position "Top centre as a vector of [x y]"]
                   [:text "Text to display"]
                   [:size "Text size as a number"]
                   [:color "Color as a vector of [red green blue]"]]}
   :triangle {:defaults {:position [[0 0] [0 0] [0 0]]}
              :doc "Creates a triangle with three corner points"
              :params [[:position "Three points as vector of [[x1 y1] [x2 y2] [x3 y3]]"]
                       [:color "Color as a vector of [red green blue]"]]}})

(def orientations [:down
                   :up
                   :left
                   :right])

(defn add-form [code form]
  (let [form (assoc form :index (count code))
        code (conj code form)]
    (println "Added form:" form)
    (println "New code:" code)
    code))

(defn add-forms [code forms]
  (reduce add-form code forms))

(defn delete [code {:keys [index] :as form}]
  (let [code (->> code
                  (remove #(= index (:index %)))
                  (into []))]
    (println "Removed form:" form)
    (println "New code:" code)
    code))

(defn replace [code {:keys [index] :as form}]
  (let [old-form (get code index)
        code (assoc code index form)]
    (println "Replaced form:" old-form)
    (println "New code:" code)
    code))

(defn reorder [code cur-index new-index]
  (let [form (nth code cur-index)
        others (remove #(= cur-index (:index %)) code)
        before (take new-index others)
        after (drop new-index others)]
    (println "Moving form" (:fun form) "from index" cur-index
             "to" new-index)
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
  (when-let [[fun & args] (read-string line)]
    (let [fun (keyword fun)
          params (map first (get-in functions [fun :params]))]
      (->> (interleave params args)
           (apply hash-map)
           (merge {:fun fun})))))

(defn read [source]
  (let [code (->> (string/split-lines source)
                  (map read-line)
                  (remove nil?)
                  (into []))]
    (println "Read code:" code)
    code))

(defn set-index [form index]
  (assoc form :index index))

(defn last-index [forms]
  (:index (last forms)))

(defn docstring [fun]
  (get-in functions [fun :doc]))
