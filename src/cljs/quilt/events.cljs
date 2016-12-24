(ns quilt.events
  (:require [quilt.code :as code]
            [quilt.db :as db]
            [quilt.library :as library]
            [re-frame.core :as rf]))

(defn- update-code [db code]
  (-> db
      (assoc :code code)
      (assoc :source (code/forms->str code))))

(rf/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-db
 :add-code
 (fn [{:keys [code] :as db} [_ form]]
   (update-code db (code/add-form code form))))

(rf/reg-event-db
 :clear-code
 (fn [db [_ form]]
   (update-code db [])))

(rf/reg-event-db
 :delete-code
 (fn [{:keys [code] :as db} [_ form]]
   (update-code db (code/delete code form))))

(rf/reg-event-db
 :eval-code
 (fn [{:keys [source] :as db} [_]]
   (assoc db :code (code/add-forms [] (code/read source)))))

(rf/reg-event-db
 :set-source
 (fn [db [_ source]]
   (assoc db :source source)))

(rf/reg-event-db
 :replace-code
 (fn [{:keys [code] :as db} [_ form]]
   (update-code db (code/replace code form))))

(rf/reg-event-db
 :load-sketch
 (fn [db [_ sketch-name]]
   (println "Loading sketch:" sketch-name)
   (update-code db (code/add-forms [] (library/sketches sketch-name)))))

(rf/reg-event-db
 :select-editor
 (fn [db [_ type]]
   (assoc-in db [:editor :type] type)))

(rf/reg-event-db
 :toggle-debug
 (fn [db [_]]
   (update-in db [:editor :debug?] not)))
