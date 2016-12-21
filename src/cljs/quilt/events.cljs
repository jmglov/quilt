(ns quilt.events
  (:require [quilt.code :as code]
            [quilt.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :add-code
 (fn [db [_ form]]
   (println "Adding code:" form)
   (update db :code code/add form)))
