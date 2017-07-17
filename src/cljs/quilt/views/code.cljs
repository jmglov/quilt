(ns quilt.views.code
  (:require [cljs.reader :refer [read-string]]
            [clojure.string :as string]
            [quilt.code :as code]
            [quilt.color :as color]
            [quilt.util :as util :refer [concatv]]
            [quilt.views.code.reorder :as reorder]
            [quilt.views.widgets :as widgets]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- delete-code [form]
  (rf/dispatch [:delete-code form])
  (rf/dispatch [:hide-docstring]))

(defn- replace-code [form]
  (rf/dispatch [:replace-code form]))

(defn- show-param-docstring [index param]
  (fn [_]
    (rf/dispatch [:show-docstring [index param]])))

(defn- input-assoc
  [size {:keys [index] :as form} [param & _ :as path] tx default]
  (-> (widgets/input #(replace-code (assoc-in form path %))
                     size (str (get-in form path)) tx default)
      (assoc-in [1 :onFocus] (show-param-docstring index param))))

(defn- input-num
  [size {:keys [index] :as form} [param & _ :as path]]
  (-> (input-assoc size form path read-string nil)
      (assoc-in [1 :class] "number-input")))

(defn- input-text [size form path]
  (input-assoc size form path identity ""))

(defn- color-picker [form]
  (let [colors (-> color/basic
                   (conj (:color form))
                   set
                   sort)]
    (concatv
     [:select
      {:value (str (:color form))
       :on-change #(let [new-color (read-string (util/get-value %))]
                     (replace-code (assoc form :color new-color)))
       :onFocus (show-param-docstring (:index form) :color)}]
     (map (fn [c] [:option (str c)]) colors))))

(defn- orientation-picker [form]
  (concatv
   [:select
    {:value (str (:orientation form))
     :on-change #(let [new-value (read-string (util/get-value %))]
                   (replace-code (assoc form :orientation new-value)))
     :onFocus (show-param-docstring (:index form) :orientation)}]
   (map (fn [c] [:option (str c)])
        code/orientations)))

(defn- render-readonly [form args]
  [(str (->> (concat [:position] args [:color])
             (map #(form %))
             (string/join " ")))])

(defn- render-circle [form readonly?]
  (if readonly?
    (render-readonly form [:radius])
    ["["
     (input-num 3 form [:position 0]) " "
     (input-num 3 form [:position 1]) "] "
     (input-num 3 form [:radius]) " "
     (color-picker form)]))

(defn- render-curve [form readonly?]
  (if readonly?
    (render-readonly form [:radius])
    ["["
     (input-num 3 form [:position 0]) " "
     (input-num 3 form [:position 1]) "] "
     (input-num 3 form [:radius]) " "
     (orientation-picker form) " "
     (input-num 2 form [:thickness]) " "
     (color-picker form)]))

(defn- render-line [form readonly?]
  (if readonly?
    (render-readonly form [:thickness])
    ["[["
     (input-num 3 form [:position 0 0]) " "
     (input-num 3 form [:position 0 1]) "] ["
     (input-num 3 form [:position 1 0]) " "
     (input-num 3 form [:position 1 1]) "]] "
     (input-num 2 form [:thickness]) " "
     (color-picker form)]))

(defn- render-rectangle [form readonly?]
  (if readonly?
    (render-readonly form [:width :height])
    ["["
     (input-num 3 form [:position 0]) " "
     (input-num 3 form [:position 1]) "] "
     (input-num 3 form [:width]) " "
     (input-num 3 form [:height]) " "
     (color-picker form)]))

(defn- render-text [form readonly?]
  (if readonly?
    (render-readonly form [:text :size])
    ["["
     (input-num 3 form [:position 0]) " "
     (input-num 3 form [:position 1]) "] \""
     (input-text 30 form [:text]) "\" "
     (input-num 2 form [:size]) " "
     (color-picker form)]))

(defn- render-triangle [form readonly?]
  (if readonly?
    (render-readonly form [])
    ["[["
     (input-num 3 form [:position 0 0]) " "
     (input-num 3 form [:position 0 1]) "] ["
     (input-num 3 form [:position 1 0]) " "
     (input-num 3 form [:position 1 1]) "] ["
     (input-num 3 form [:position 2 0]) " "
     (input-num 3 form [:position 2 1]) "]] "
     (color-picker form)]))

(defn render [{:keys [fun index] :as form} editor-atom sketch]
  (let [{:keys [readonly? selected-index]} @editor-atom
        css-classes (str "form unselectable"
                         (when (= selected-index index) " selected-form"))]
    (concatv
     [:div
      (concatv
       [:div {:class css-classes}]
       (when-not (:readonly? @editor-atom)
         [[:div.drag-handle
           {:on-mouse-down (reorder/mouse-down-handler form editor-atom)}
           "↕"]])
       [(concatv
         [:div (str "(" (name fun) " ")]
         (case fun
           :circle (render-circle form readonly?)
           :curve (render-curve form readonly?)
           :line (render-line form readonly?)
           :rectangle (render-rectangle form readonly?)
           :text (render-text form readonly?)
           :triangle (render-triangle form readonly?))
         [")"])]
       [[:div.delete-form {:on-click #(delete-code form)} "✖"]])]
     (when-let [[displayed-index param] (:displayed-docstring @editor-atom)]
       (when (and (= displayed-index index)
                  (not (:readonly? @editor-atom)))
         [[:div.docstring (code/docstring fun param)]])))))
