(ns quilt.code
  (:require [cljs.reader :refer [read-string]]
            [clojure.string :as string]
            [quilt.color :as color])
  (:refer-clojure :exclude [replace]))

(def functions
  {:circle {:defaults {:position [0 0]
                       :radius 0}
            :params [:position
                     :radius
                     :color]}
   :curve {:defaults {:position [[0 0] [0 0]]
                      :orientation :down
                      :thickness 10}
           :params [:position
                    :orientation
                    :thickness
                    :color]}
   :line {:defaults {:position [[0 0] [0 0]]
                     :thickness 10}
          :params [:position
                   :thickness
                   :color]}
   :rectangle {:defaults {:position [0 0]
                          :width 0
                          :height 0}
               :params [:position
                        :width
                        :height
                        :color]}
   :text {:defaults {:position [0 0]
                     :text ""
                     :size 24}
          :params [:position
                   :text
                   :size
                   :color]}
   :triangle {:defaults {:position [[0 0] [0 0] [0 0]]}
              :params [:position
                       :color]}})

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

(defn create-form [fun]
  (merge {:fun fun
          :color color/default}
         (get-in functions [fun :defaults])))

(defn- form->str [{:keys [fun] :as form}]
  form
  (str "(" (name fun) " "
       (->> (get-in functions [fun :params])
            (map #(pr-str (form %)))
            (string/join " "))
       ")"))

(defn forms->str [forms]
  (->> forms
       (map form->str)
       (string/join "\n")))

(defn- read-line [line]
  (when-let [[fun & args] (read-string line)]
    (let [fun (keyword fun)
          params (get-in functions [fun :params])]
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
