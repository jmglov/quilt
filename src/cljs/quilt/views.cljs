(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [quilt.code :refer [create-form]]
            [quilt.sketch :as sketch :refer [sketch]]
            [quilt.util :refer [concatv get-value]]
            [quilt.views.code :as code]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- forms []
  (let [code (rf/subscribe [:code])]
    (fn []
      (concatv [:div.outlined]
               (mapv code/render @code)))))

(defn- modify-forms []
  (let [new-fun (r/atom :clear)
        add-code #(rf/dispatch [:add-code (create-form @new-fun)])
        clear-code #(rf/dispatch [:clear-code])]
    (fn []
      [:div#modify-forms.container
       [:div.outlined
        [:select
         {:value (name @new-fun)
          :on-change #(reset! new-fun (keyword (get-value %)))}
         [:option "clear"]
         [:option "color"]
         [:option "text"]
         [:option "circle"]]
        [:button {:on-click add-code} "Add"]]
       [:button {:on-click clear-code} "Delete all"]])))

(defn- code-list []
  (let [code (rf/subscribe [:code])]
    (fn []
      (concatv [:div.outlined]
               (mapv (fn [c]
                       [:div (with-out-str (pprint c))])
                     @code)))))

(defn main-panel []
  (fn []
    [:div.container
     [sketch]
     [:div#editor
      [:h2 "Code"]
      [forms]
      [modify-forms]
      [:h2 "Debug"]
      [code-list]]]))
