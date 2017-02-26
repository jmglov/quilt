(ns quilt.views.debug
  (:require [cljs.pprint :refer [pprint]]
            [clojure.string :as string]
            [quilt.i18n :as i18n]
            [quilt.util :as util :refer [concatv]]
            [re-frame.core :as rf]))

(defn debug []
  (let [db-atom (rf/subscribe [:db])
        editor-atom (rf/subscribe [:editor])
        indentation (fn [s] (count (take-while #(= % \space) s)))]
    (fn []
      (when (:debug? @editor-atom)
        [:div#debug
         [:h2 (i18n/str "Debug")]
         (concatv [:div.outlined]
                  (mapv (fn [line]
                          [:div
                           {:style {:padding-left (str (indentation line) "em")}}
                           line])
                        (->  (pprint @db-atom)
                             with-out-str
                             (string/split "\n"))))]))))
