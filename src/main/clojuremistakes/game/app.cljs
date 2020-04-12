(ns clojuremistakes.game.app
  (:require
   [clojuremistakes.game.input :as input]
   [clojuremistakes.game.game :as game]
   [clojuremistakes.game.pixi :as pixi-utils]
   ["pixi.js" :as pixi]))

(def width 800)
(def height 600)



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

(defn game-loop [game]
  (let []
    (js/requestAnimationFrame
     (fn [ts]
       (let [ts (* ts 0.001)]
         (.clear (:graphics @state))
         (draw-circle {:radius (* 3 ts) :x (* 2 ts) :y (* 2 ts) :backgroundColor (.toString (/ 255 ts) 16)})
         (game-loop (assoc game
                           :delta-time (- ts (:total-time game))
                           :total-time ts)))))))

(def context
  (let [{:keys [graphics
                app
                container]} @state]
    (draw-circle {:x (/ width 2)
                  :y (/ height 2)
                  :radius 10
                  :backgroundColor 0x00ff00})
    (.addChild (.-stage app) graphics)
    (game-loop {})))

(defn init []
  (println "Hello world")
  (println @state))