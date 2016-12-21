(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [quilt.code :refer [create-form]]
            [quilt.sketch :as sketch :refer [sketch]]
            [quilt.views.code :as code]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn- concatv [& vs]
  (into [] (apply concat vs)))

(defn- forms []
  (let [code (rf/subscribe [:code])]
    (fn []
      (concatv [:div]
               (mapv code/render @code)))))

(defn- new-form []
  (let [new-fun (r/atom :clear)
        add-code #(rf/dispatch [:add-code (create-form @new-fun)])
        get-value #(-> % .-target .-value)]
    (fn []
      [:div
       [:select
        {:value (name @new-fun)
         :on-change #(reset! new-fun (keyword (get-value %)))}
        [:option "clear"]
        [:option "color"]
        [:option "text"]
        [:option "circle"]]
       [:button {:on-click add-code}
        "Add"]])))

(defn- code-list []
  (let [code (rf/subscribe [:code])]
    (fn []
      (concatv [:div]
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
      [new-form]
      [:h2 "Debug"]
      [code-list]]]))
