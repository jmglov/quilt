(ns quilt.events
  (:require [quilt.code :as code]
            [quilt.config :as config]
            [quilt.db :as db]
            [quilt.library :as library]
            [re-frame.core :as rf]))

(defn- update-code
  ([db new-code]
   (update-code db new-code :with-undo))
  ([{:keys [code undo redo] :as db} new-code with-undo?]
   (-> db
       (assoc :code new-code)
       (assoc :undo (if with-undo? (conj undo code) undo))
       (assoc :redo (if with-undo? [] redo))
       (assoc :source (code/forms->str new-code)))))

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
 (fn [{:keys [code] :as db} [_ old-index new-index]]
   (update-code db (code/reorder code old-index new-index))))

(rf/reg-event-db
 :clear-code
 (fn [db [_ form]]
   (update-code db [])))

(rf/reg-event-db
 :eval-code
 (fn [{:keys [source] :as db} [_]]
   (if-let [code (code/read source)]
     (update-code db (code/add-forms [] code))
     db)))

(rf/reg-event-db
 :undo
 (fn [{:keys [code redo undo] :as db} [_]]
   (-> db
       (assoc :redo (conj redo code))
       (assoc :undo (pop undo))
       (update-code (peek undo) false))))

(rf/reg-event-db
 :redo
 (fn [{:keys [code redo undo] :as db} [_]]
   (-> db
       (assoc :redo (pop redo))
       (assoc :undo (conj undo code))
       (update-code (peek redo) false))))


;; Visual editor
;; =============================================================================

(rf/reg-event-db
 :select-form
 (fn [db [_ index]]
   (assoc-in db [:editor :selected-index] index)))

(rf/reg-event-db
 :unselect-form
 (fn [db [_]]
   (assoc-in db [:editor :selected-index] nil)))

(defn- set-docstring-path [db path]
  (assoc-in db [:editor :displayed-docstring] path))

(rf/reg-event-db
 :show-docstring
 (fn [db [_ path]]
   (set-docstring-path db path)))

(rf/reg-event-db
 :show-docstring-new-form
 (fn [{:keys [code] :as db} [_]]
   (set-docstring-path db [(code/last-index code)])))

(rf/reg-event-db
 :hide-docstring
 (fn [db [_]]
   (assoc-in db [:editor :displayed-docstring] nil)))

;; Source editor
;; =============================================================================

(rf/reg-event-db
 :set-source
 (fn [db [_ source]]
   (assoc db :source source)))

(rf/reg-event-db
 :reset-source
 (fn [{:keys [code] :as db} [_]]
   (let [source (code/forms->str code)]
     (assoc db :source source))))


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

(rf/reg-event-db
 :toggle-readonly
 (fn [db [_]]
   (update-in db [:editor :readonly?] not)))


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
   (-> db
       (update-code (code/add-forms [] (library/sketches sketch-name)))
       (assoc-in [:sketch :size] config/default-sketch-size))))

(rf/reg-event-db
 :set-sketch-size
 (fn [db [_ width height]]
   (println "Setting sketch size:" width height)
   (assoc-in db [:sketch :size] [width height])))


;; Language
;; =============================================================================

(rf/reg-event-db
 :select-lang
 (fn [db [_ lang]]
   (assoc db [:language] lang)))
