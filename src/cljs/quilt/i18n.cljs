(ns quilt.i18n
  (:require [re-frame.core :as rf])
  (:refer-clojure :exclude [str]))

(def languages [[:en-GB "English"]
                [:sv-SE "Svenska"]])

(defn language-code [lang]
  (some (fn [[code label]] (when (= lang label) code))
        languages))

(defn language-label [lang]
  (some (fn [[code label]] (when (= lang code) label))
        languages))

(def ^:private translations
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
   {:sv-SE "Visuell"}

   "color as a keyword or vector of [:red :green :blue]"
   {:sv-SE "Färg som ett nyckelord eller som en vektor av [:red :green :blue]"}

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

   "centre point as a vector of [x y]"
   {:sv-SE "mittpunkt som en vektor av [x y]"}

   "height as a number"
   {:sv-SE "höjd som en siffra"}

   "one of :up, :down, :left or :right"
   {:sv-SE "en av :up, :down, :left eller :right"}

   "radius as a number"
   {:sv-SE "radie som en siffra"}

   "starting and ending points as vector of [[x1 y1] [x2 y2]]"
   {:sv-SE "start- och slutpunkt som en vektor av [[x1 y1] [x2 y2]]"}

   "thickness as a number"
   {:sv-SE "tjocklek som en siffra"}

   "three points as vector of [[x1 y1] [x2 y2] [x3 y3]]"
   {:sv-SE "tre punkter som en vektor av [[x1 y1] [x2 y2] [x3 y3]]"}

   "text to display"
   {:sv-SE "text att visa"}

   "text size as a number"
   {:sv-SE "textstorlek som en siffra"}

   "top centre as a vector of [x y]"
   {:sv-SE "övre mittpunkt som en vektor av [x y]"}

   "upper left corner as a vector of [x y]"
   {:sv-SE "övre vänstra hörnet som en vektor av [x y]"}

   "width as a number"
   {:sv-SE "bredd som en siffra"}})

(defn str [string]
  (let [language (rf/subscribe [:language])]
    (get-in translations [string (keyword @language)] string)))
