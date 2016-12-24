(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [quilt.code :as code]
            [quilt.sketch :as sketch :refer [sketch]]
            [quilt.util :refer [concatv get-value]]
            [quilt.views.code :as views.code]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- clear-code []
  (rf/dispatch [:clear-code]))

(defn- forms []
  (let [code-atom (rf/subscribe [:code])]
    (fn []
      (concatv [:div#forms.outlined]
               (mapv views.code/render @code-atom)))))

(defn- modify-forms []
  (let [new-fun (r/atom (first code/functions))
        add-code #(rf/dispatch [:add-code (code/create-form @new-fun)])]
    (fn []
      [:div#modify-forms.container
       [:div.outlined
        (concatv
         [:select
          {:value (name @new-fun)
           :on-change #(reset! new-fun (keyword (get-value %)))}]
         (map (fn [fun] [:option (name fun)]) code/functions))
        [:button {:on-click add-code} "Add"]]
       [:button {:on-click clear-code} "Delete all"]])))

(defn- code-editor []
  (let [code-atom (rf/subscribe [:code])
        source (r/atom (code/forms->str @code-atom))
        eval-code #(rf/dispatch [:eval-code @source])]
    (fn []
      [:div#source-editor
       [:textarea {:value @source
                   :on-change #(reset! source (get-value %))}]
       [:div.container
        [:button {:on-click eval-code} "Eval"]
        [:button {:on-click clear-code} "Clear"]]])))

(defn- code-list []
  (let [code-atom (rf/subscribe [:code])]
    (fn []
      (concatv [:div.outlined]
               (mapv (fn [c]
                       [:div (with-out-str (pprint c))])
                     @code-atom)))))

(defn main-panel []
  (fn []
    [:div.container
     [sketch]
     [:div#editor
      [:h2 "Code"]
      [forms]
      [modify-forms]
      [:h2 "Editor"]
      [code-editor]
      [:h2 "Debug"]
      [code-list]]]))
