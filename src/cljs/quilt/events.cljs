(ns quilt.events
  (:require [quilt.code :as code]
            [quilt.db :as db]
            [quilt.library :as library]
            [re-frame.core :as rf]))

(rf/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-db
 :add-code
 (fn [db [_ form]]
   (update db :code code/add-form form)))

(rf/reg-event-db
 :clear-code
 (fn [db [_ form]]
   (assoc db :code [])))

(rf/reg-event-db
 :delete-code
 (fn [db [_ form]]
   (update db :code code/delete form)))

(rf/reg-event-db
 :eval-code
 (fn [db [_ source]]
   (assoc db :code (code/add-forms [] (code/read source)))))

(rf/reg-event-db
 :replace-code
 (fn [db [_ form]]
   (update db :code code/replace form)))

(rf/reg-event-db
 :load-sketch
 (fn [db [_ sketch-name]]
   (println "Loading sketch:" sketch-name)
   (assoc db :code (code/add-forms [] (library/sketches sketch-name)))))

(rf/reg-event-db
 :select-editor
 (fn [db [_ type]]
   (assoc-in db [:editor :type] type)))

(rf/reg-event-db
 :toggle-debug
 (fn [db [_]]
   (update-in db [:editor :debug?] not)))
