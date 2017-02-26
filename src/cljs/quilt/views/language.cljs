(ns quilt.views.language
  (:require [quilt.i18n :as i18n]
            [quilt.util :as util :refer [concatv get-value]]
            [re-frame.core :as rf]))

(defn- language []
  (let [lang-atom (rf/subscribe [:language])
        select-lang #(rf/dispatch
                      [:select-lang
                       (i18n/language-code (get-value %))])]
    (fn []
      [:div#lang.container
       [:div (str (i18n/str "Language") ": " (i18n/language-label @lang-atom))]
       ;; TODO: dynamically change language
       #_(concatv [:select {:value (str @lang-atom)
                            :on-change select-lang}]
                  (mapv (fn [[code label]]
                          [:option {:value code} label])
                        i18n/languages))])))
