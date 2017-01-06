(ns quilt.sketch.resolution
  "The sketch can be in either normal or lo-res mode. In either case, numbers
   are stored in the app state in full resolution. When updating the app state
   from the UI, use the scale function. When updating the UI from the app
   state, use the display function.")

(def ^:private scaling-factor 10)

(defn scale
  "Returns a number or set of coordinates scaled up to full resolution"
  [ {:keys [lo-res?] :as sketch} n]
  (if (vector? n)
    (mapv #(scale sketch %) n)
    (if lo-res? (* n scaling-factor) n)))

(defn display
  "Returns a number or set of coordinates scaled down to the sketch resolution"
  [{:keys [lo-res?] :as sketch} n]
  (if (vector? n)
    (mapv #(display sketch %) n)
    (if lo-res? (quot n scaling-factor) n)))
