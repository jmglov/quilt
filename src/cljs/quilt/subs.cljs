(ns quilt.subs
  (:require [re-frame.core :as re-frame])
  (:require-macros [reagent.ratom :refer [reaction]]))

(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 :code
 (fn [db]
   (:code db)))
