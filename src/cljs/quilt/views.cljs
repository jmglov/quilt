(ns quilt.views
  (:require [cljs.pprint :refer [pprint]]
            [cljs.reader :refer [read-string]]
            [quilt.sketch :as sketch :refer [sketch]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn code-pane []
  (let [code (rf/subscribe [:code])
        new-form (r/atom "")
        add-code #(do
                    (rf/dispatch [:add-code (read-string @new-form)])
                    (reset! new-form ""))
        get-value #(-> % .-target .-value)]
    (fn []
      (into []
            (concat [:div [:h2 "Code"]]
                    (map (fn [c] [:div (with-out-str (pprint c))])
                         @code)
                    [[:div
                      [:input {:type "text"
                               :size "64"
                               :value @new-form
                               :on-change #(reset! new-form (get-value %))
                               :on-key-press (fn [e]
                                               (when (= 13 (.-charCode e))
                                                 (add-code)))}]]])))))

(defn main-panel []
  (fn []
    [:div
     [sketch]
     [code-pane]]))
