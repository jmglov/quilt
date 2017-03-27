(ns quilt.views.library
  (:require [quilt.i18n :as i18n]
            [quilt.library :as library]
            [quilt.util :refer [concatv get-value]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn library []
  (let [sketch-name (r/atom (first (keys library/sketches)))
        set-sketch #(reset! sketch-name (keyword (get-value %)))]
    (fn []
      [:div#library.container
       (str (i18n/str "Load drawing") ":")
       (concatv [:select {:value (name @sketch-name)
                          :on-change set-sketch}]
                (-> (mapv (fn [s] [:option (name s)])
                          (keys library/sketches))
                    (conj [:option (i18n/str "random")])))
       [:button {:on-click #(rf/dispatch [:load-sketch @sketch-name])}
        (i18n/str "OK")]])))
