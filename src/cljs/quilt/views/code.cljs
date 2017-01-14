(ns quilt.views.code
  (:require [cljs.reader :refer [read-string]]
            [clojure.string :as string]
            [quilt.code :as code]
            [quilt.color :as color]
            [quilt.sketch.resolution :as resolution]
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
    (println (str "Form " index " param " param " focused"))
    (rf/dispatch [:show-docstring [index param]])))

(defn- input-assoc
  [size {:keys [index] :as form} [param & _ :as path] tx default scale display]
  (-> (widgets/input #(replace-code (assoc-in form path (scale %)))
                     size (str (display (get-in form path))) tx default)
      (assoc-in [1 :onFocus] (show-param-docstring index param))))

(defn- input-num
  [size {:keys [index] :as form} [param & _ :as path] scale display]
  (input-assoc size form path read-string 0 scale display))

(defn- input-text [size form path]
  (input-assoc size form path identity "" identity identity))

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

(defn- render-readonly [form args display]
  (let [pr-val (fn [v] (if (string? v) v (display v)))]
    [(str (->> (concat [:position] args [:color])
               (map #(pr-val (form %)))
               (string/join " ")))]))

(defn- render-circle [form readonly? scale display]
  (if readonly?
    (render-readonly form [:radius] display)
    ["["
     (input-num 3 form [:position 0] scale display) " "
     (input-num 3 form [:position 1] scale display) "] "
     (input-num 3 form [:radius] scale display) " "
     (color-picker form)]))

(defn- render-curve [form readonly? scale display]
  (if readonly?
    (render-readonly form [:radius] display)
    ["["
     (input-num 3 form [:position 0] scale display) " "
     (input-num 3 form [:position 1] scale display) "] "
     (input-num 3 form [:radius] scale display) " "
     (orientation-picker form) " "
     (input-num 2 form [:thickness] scale display) " "
     (color-picker form)]))

(defn- render-line [form readonly? scale display]
  (if readonly?
    (render-readonly form [:thickness] display)
    ["[["
     (input-num 3 form [:position 0 0] scale display) " "
     (input-num 3 form [:position 0 1] scale display) "] ["
     (input-num 3 form [:position 1 0] scale display) " "
     (input-num 3 form [:position 1 1] scale display) "]] "
     (input-num 2 form [:thickness] scale display) " "
     (color-picker form)]))

(defn- render-rectangle [form readonly? scale display]
  (if readonly?
    (render-readonly form [:width :height] display)
    ["["
     (input-num 3 form [:position 0] scale display) " "
     (input-num 3 form [:position 1] scale display) "] "
     (input-num 3 form [:width] scale display) " "
     (input-num 3 form [:height] scale display) " "
     (color-picker form)]))

(defn- render-text [form readonly? scale display]
  (if readonly?
    (render-readonly form [:text :size] display)
    ["["
     (input-num 3 form [:position 0] scale display) " "
     (input-num 3 form [:position 1] scale display) "] "
     (input-text 30 form [:text]) " "
     (input-num 2 form [:size] scale display) " "
     (color-picker form)]))

(defn- render-triangle [form readonly? scale display]
  (if readonly?
    (render-readonly form [] display)
    ["[["
     (input-num 3 form [:position 0 0] scale display) " "
     (input-num 3 form [:position 0 1] scale display) "] ["
     (input-num 3 form [:position 1 0] scale display) " "
     (input-num 3 form [:position 1 1] scale display) "] ["
     (input-num 3 form [:position 2 0] scale display) " "
     (input-num 3 form [:position 2 1] scale display) "]] "
     (color-picker form)]))

(defn render [{:keys [fun index] :as form} editor-atom sketch]
  (let [scale #(resolution/scale sketch %)
        display #(resolution/display sketch %)
        {:keys [readonly? selected-index]} @editor-atom
        css-classes (str "form unselectable container"
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
           :circle (render-circle form readonly? scale display)
           :curve (render-curve form readonly? scale display)
           :line (render-line form readonly? scale display)
           :rectangle (render-rectangle form readonly? scale display)
           :text (render-text form readonly? scale display)
           :triangle (render-triangle form readonly? scale display))
         [")"])]
       [[:div.delete-form {:on-click #(delete-code form)} "✖"]])]
     (when-let [[displayed-index param] (:displayed-docstring @editor-atom)]
       (when (and (= displayed-index index)
                  (not (:readonly? @editor-atom)))
         [[:div.docstring (code/docstring fun param)]])))))
