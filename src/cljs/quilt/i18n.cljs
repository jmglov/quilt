(ns quilt.i18n
  (:require [re-frame.core :as rf])
  (:refer-clojure :exclude [str]))

(def translations
  {"Add"
   {:sv-SE "Lägg till"}

   "Clear"
   {:sv-SE "Rensa"}

   "Code"
   {:sv-SE "Kod"}

   "circle"
   {:sv-SE "cirkel"}

   "curve"
   {:sv-SE "kurva"}

   "Click drawing to show current position"
   {:sv-SE "Klicka på bilden för att visa nuvarande position"}

   "Click drawing to save current position"
   {:sv-SE "Klicka på bilden för att spara nuvarande position"}

   "Current position"
   {:sv-SE "Nuvarande position"}

   "Debug"
   {:sv-SE "Debug"}

   "Drawing size"
   {:sv-SE "Bildstorlek"}

   "Delete all"
   {:sv-SE "Ta bort alla"}

   "Editor"
   {:sv-SE "Editor"}

   "Eval"
   {:sv-SE "Evaluera"}

   "Forms"
   {:sv-SE "Formulär"}

   "OK"
   {:sv-SE "OK"}

   "Language"
   {:sv-SE "Språk"}

   "line"
   {:sv-SE "linje"}

   "Load drawing"
   {:sv-SE "Ladda teckning"}

   "Read only?"
   {:sv-SE "Skrivskydda?"}

   "Reset"
   {:sv-SE "Återställ"}

   "rectangle"
   {:sv-SE "rektangel"}

   "Show debug?"
   {:sv-SE "Visa debug?"}

   "Source"
   {:sv-SE "Källkod"}

   "text"
   {:sv-SE "text"}

   "triangle"
   {:sv-SE "triangel"}

   "Visual"
   {:sv-SE "Visuell"}})

(defn str [string]
  (let [language (rf/subscribe [:language])]
    (get-in translations [string (keyword @language)] string)))
