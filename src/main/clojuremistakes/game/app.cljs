(ns clojuremistakes.game.app
  (:require
   [clojuremistakes.game.circles.game :as circles-demo]
   [clojuremistakes.game.mousemovementdemo.game :as movement-demo]
   [clojuremistakes.game.cellularautomata.game :as cellular-demo]
   ["pixi.js" :as pixi]))

(defn init []
  (let []
    ; Start the circles-demo
    ;(circles-demo/start-game)
    ;(movement-demo/start-game)
    (cellular-demo/start-game)))