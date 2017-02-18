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

   "Click drawing to show current position"
   {:sv-SE "Klicka på bilden för att visa nuvarande position"}

   "Click drawing to save current position"
   {:sv-SE "Klicka på bilden för att spara nuvarande position"}

   "Current position"
   {:sv-SE "Nuvarande position"}

   "creates a circle around a central point"
   {:sv-SE "skapar en cirkel runt en central punkt"}

   "creates a circular curve around a central point"
   {:sv-SE "skapar en cirkulär kurva runt en central punkt"}

   "creates a line from a starting point to an ending point"
   {:sv-SE "skapar en linje från en startpunkt till en slutpunkt"}

   "creates a rectangle from an upper left corner point"
   {:sv-SE "skapar en rektangel från en punkt i övre vänstra hörnet"}

   "creates some text at a top center point"
   {:sv-SE "skapar text vid en punkt i övre kantens mitt"}

   "creates a triangle with three corner points"
   {:sv-SE "skapar en triangel med tre hörnpunkter"}

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

   "Load drawing"
   {:sv-SE "Ladda teckning"}

   "Read only?"
   {:sv-SE "Skrivskydda?"}

   "Reset"
   {:sv-SE "Återställ"}

   "Show debug?"
   {:sv-SE "Visa debug?"}

   "Source"
   {:sv-SE "Källkod"}

   "Visual"
   {:sv-SE "Visuell"}})

(defn str [string]
  (let [language (rf/subscribe [:language])]
    (get-in translations [string (keyword @language)] string)))
