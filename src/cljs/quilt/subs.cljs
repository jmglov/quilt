(ns quilt.subs
  (:require [re-frame.core :as rf])
  (:require-macros [reagent.ratom :refer [reaction]]))

(rf/reg-sub :db identity)  ; return the entire db

(rf/reg-sub :code :code)
(rf/reg-sub :undo :undo)
(rf/reg-sub :redo :redo)
(rf/reg-sub :editor :editor)
(rf/reg-sub :language :language)
(rf/reg-sub :mouse :mouse)
(rf/reg-sub :simple-ui? :simple-ui?)
(rf/reg-sub :sketch :sketch)
(rf/reg-sub :source :source)
