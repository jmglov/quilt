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

;; Code handling
;; =============================================================================

(rf/reg-event-db
 :add-code
 (fn [{:keys [code] :as db} [_ form]]
   (update-code db (code/add-form code form))))

(rf/reg-event-db
 :replace-code
 (fn [{:keys [code] :as db} [_ form]]
   (update-code db (code/replace code form))))

(rf/reg-event-db
 :delete-code
 (fn [{:keys [code] :as db} [_ form]]
   (update-code db (code/delete code form))))

(rf/reg-event-db
 :reorder-code
 (fn [{:keys [code] :as db} [_ form index]]
   (update-code db (code/reorder code form index))))

(rf/reg-event-db
 :clear-code
 (fn [db [_ form]]
   (update-code db [])))

(rf/reg-event-db
 :eval-code
 (fn [{:keys [source] :as db} [_]]
   (assoc db :code (code/add-forms [] (code/read source)))))


;; Source editor
;; =============================================================================

(rf/reg-event-db
 :set-source
 (fn [db [_ source]]
   (assoc db :source source)))


;; Editor config
;; =============================================================================

(rf/reg-event-db
 :select-editor
 (fn [db [_ type]]
   (assoc-in db [:editor :type] type)))

(rf/reg-event-db
 :toggle-debug
 (fn [db [_]]
   (update-in db [:editor :debug?] not)))


;; Position tracking
;; =============================================================================

(rf/reg-event-db
 :lock-mouse-pos
 (fn [db [_]]
   (update-in db [:mouse :locked?] not)))

(rf/reg-event-db
 :set-mouse-pos
 (fn [db [_ pos]]
   (if (get-in db [:mouse :locked?])
     db
     (assoc-in db [:mouse :pos] pos))))


;; Sketch config
;; =============================================================================

(rf/reg-event-db
 :load-sketch
 (fn [db [_ sketch-name]]
   (println "Loading sketch:" sketch-name)
   (update-code db (code/add-forms [] (library/sketches sketch-name)))))

(rf/reg-event-db
 :set-sketch-size
 (fn [db [_ width height]]
   (println "Setting sketch size:" width height)
   (assoc-in db [:sketch :size] [width height])))
