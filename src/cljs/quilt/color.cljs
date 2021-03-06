(ns quilt.color
  (:require [clojure.string :as string]))

(def default :black)

(def basic
  [:black
   :blue
   :brown
   :green
   :grey
   :orange
   :pink
   :purple
   :red
   :white
   :yellow])

(def color
  {:aquamarine [127 255 212]
   :medium-violet-red [199 21 133]
   :pale-violet-red [219 112 147]
   :lime [0 255 0]
   :dark-golden-rod [184 134 11]
   :sandy-brown [244 164 96]
   :sienna [160 82 45]
   :orange [255 165 0]
   :medium-turquoise [72 209 204]
   :rosy-brown [188 143 143]
   :yellow-green [154 205 50]
   :alice-blue [240 248 255]
   :orange-red [255 69 0]
   :sea-shell [255 245 238]
   :deep-sky-blue [0 191 255]
   :lime-green [50 205 50]
   :deep-pink [255 20 147]
   :blue-violet [138 43 226]
   :dark-orange [255 140 0]
   :gray [128 128 128]
   :sea-green [46 139 87]
   :light-pink [255 182 193]
   :sky-blue [135 206 235]
   :green-yellow [173 255 47]
   :tan [210 180 140]
   :dark-slate-gray [47 79 79]
   :dark-slate-grey [47 79 79]
   :light-salmon [255 160 122]
   :bisque [255 228 196]
   :white [255 255 255]
   :midnight-blue [25 25 112]
   :peach-puff [255 218 185]
   :lavender-blush [255 240 245]
   :slate-gray [112 128 144]
   :dim-grey [105 105 105]
   :pale-turquoise [175 238 238]
   :dark-orchid [153 50 204]
   :crimson [220 20 60]
   :antique-white [250 235 215]
   :chocolate [210 105 30]
   :light-slate-grey [119 136 153]
   :dark-sea-green [143 188 143]
   :light-gray [211 211 211]
   :yellow [255 255 0]
   :floral-white [255 250 240]
   :medium-purple [147 112 219]
   :indian-red [205 92 92]
   :medium-spring-green [0 250 154]
   :navy [0 0 128]
   :old-lace [253 245 230]
   :green [0 128 0]
   :indigo [75 0 130]
   :medium-slate-blue [123 104 238]
   :cyan [0 255 255]
   :saddle-brown [139 69 19]
   :burly-wood [222 184 135]
   :violet [238 130 238]
   :light-coral [240 128 128]
   :dark-olive-green [85 107 47]
   :spring-green [0 255 127]
   :white-smoke [245 245 245]
   :dark-red [139 0 0]
   :ivory [255 255 240]
   :salmon [250 128 114]
   :slate-grey [112 128 144]
   :honey-dew [240 255 240]
   :dark-violet [148 0 211]
   :peru [205 133 63]
   :cornsilk [255 248 220]
   :dark-gray [169 169 169]
   :navajo-white [255 222 173]
   :misty-rose [255 228 225]
   :gold [255 215 0]
   :gainsboro [220 220 220]
   :chartreuse [127 255 0]
   :lemon-chiffon [255 250 205]
   :snow [255 250 250]
   :moccasin [255 228 181]
   :aqua [0 255 255]
   :dark-grey [169 169 169]
   :dodger-blue [30 144 255]
   :dim-gray [105 105 105]
   :wheat [245 222 179]
   :hot-pink [255 105 180]
   :light-golden-rod-yellow [250 250 210]
   :light-steel-blue [176 196 222]
   :light-grey [211 211 211]
   :turquoise [64 224 208]
   :royal-blue [65 105 225]
   :red [255 0 0]
   :blue [0 0 255]
   :mint-cream [245 255 250]
   :medium-blue [0 0 205]
   :khaki [240 230 140]
   :maroon [128 0 0]
   :rebecca-purple [102 51 153]
   :cornflower-blue [100 149 237]
   :dark-magenta [139 0 139]
   :light-slate-gray [119 136 153]
   :dark-green [0 100 0]
   :azure [240 255 255]
   :medium-orchid [186 85 211]
   :fuchsia [255 0 255]
   :fire-brick [178 34 34]
   :coral [255 127 80]
   :dark-blue [0 0 139]
   :orchid [218 112 214]
   :plum [221 160 221]
   :pink [255 192 203]
   :teal [0 128 128]
   :medium-sea-green [60 179 113]
   :lawn-green [124 252 0]
   :magenta [255 0 255]
   :forest-green [34 139 34]
   :light-green [144 238 144]
   :dark-cyan [0 139 139]
   :dark-turquoise [0 206 209]
   :light-blue [173 216 230]
   :slate-blue [106 90 205]
   :powder-blue [176 224 230]
   :purple [128 0 128]
   :olive-drab [107 142 35]
   :ghost-white [248 248 255]
   :steel-blue [70 130 180]
   :golden-rod [218 165 32]
   :cadet-blue [95 158 160]
   :pale-green [152 251 152]
   :thistle [216 191 216]
   :light-sky-blue [135 206 250]
   :blanched-almond [255 235 205]
   :light-cyan [224 255 255]
   :silver [192 192 192]
   :grey [128 128 128]
   :dark-slate-blue [72 61 139]
   :dark-salmon [233 150 122]
   :brown [165 42 42]
   :olive [128 128 0]
   :tomato [255 99 71]
   :linen [250 240 230]
   :medium-aqua-marine [102 205 170]
   :lavender [230 230 250]
   :papaya-whip [255 239 213]
   :pale-golden-rod [238 232 170]
   :dark-khaki [189 183 107]
   :beige [245 245 220]
   :black [0 0 0]
   :light-sea-green [32 178 170]
   :light-yellow [255 255 224]})

(defn ->html-color [c]
  (if (vector? c)
    (let [[r g b] c]
      (str "#"
           (.toString r 16)
           (.toString g 16)
           (.toString b 16)))
    (string/replace (name c) "-" "")))
