(ns clojuremistakes.game.app
  (:require
   [clojuremistakes.game.input :as input]
   [clojuremistakes.game.game :as game]
   [clojuremistakes.game.pixi :as pixi-utils]
   ["pixi.js" :as pixi]))

(def width 800)
(def height 600)
(def hex-color-max 16777216)
(defonce default-circle {:x 50
                         :y 50
                         :radius 100
                         :borderWidth 1
                         :borderColor 0xff0000
                         :borderAlpha 1
                         :backgroundColor 0xff0000})

(defonce state (atom (let [canvas (js/document.getElementById "game_canvas")
                           app (pixi-utils/pixi-application width height canvas)]
                       {:graphics (pixi/Graphics.)
                        :canvas canvas
                        :app app
                        :container (pixi-utils/create-container app)})))

(defn draw-circle [circle]
  (let [{:keys [x
                y
                radius
                borderWidth
                borderColor
                borderAlpha
                backgroundColor]} (merge default-circle circle)
        {:keys [graphics
                container]} @state]
    (.lineStyle graphics borderWidth borderColor borderAlpha)
    (.beginFill graphics backgroundColor)
    (.drawCircle graphics x y radius)
    (.endFill graphics)))

; (defn game-loop [game]
;   (let []
;     (js/requestAnimationFrame
;      (fn [ts]
;        (let [ts (* ts 0.001)]
;          (.clear (:graphics @state))
;          (draw-circle {:radius (* 3 ts) :x (* 2 ts) :y (* 2 ts) :backgroundColor (.toString (:total-time game) 16)})
;          (game-loop (assoc game
;                            :delta-time (- ts (:total-time game))
;                            :total-time ts)))))))

(defn init-game-loop [on-tick]
  (println (:app @state))
  (.add (.-ticker (:app @state)) on-tick))

(defonce my-persistent-circle (atom {:x 10 :y 10 :radius 10 :backgroundColor 0x87ceeb}))

(defn move-circle [delta]
  (swap! my-persistent-circle assoc
         :x (+ delta (:x @my-persistent-circle))
         :y (+ delta (:y @my-persistent-circle))))

(defn render-game [delta]
  (draw-circle @my-persistent-circle)
  (println (str "Drawing circle at " (:x @my-persistent-circle) "," (:y @my-persistent-circle))))

(defn init []
  (let [{:keys [graphics
                app]} @state]
    (println "Starting game...")
    (println "Adding shared grapics object to PIXI.Stage")
    (.addChild (.-stage app) graphics)
    (println "Initializing game loop")
    (init-game-loop
     (fn [delta]
       (.clear (:graphics @state))
       (move-circle delta)
       (render-game delta)))))