(ns clojuremistakes.game.app
  (:require
   #_[clojuremistakes.game.circles.game :as circles-demo]
   #_[clojuremistakes.game.mousemovementdemo.game :as movement-demo]
   #_[clojuremistakes.game.cellularautomata.game :as cellular-demo]
   [clojuremistakes.game.logic.game :as logic]
   ["pixi.js" :as pixi]))

(defn init []
  (let []
    ; Start the circles-demo
    ;(circles-demo/start-game)
    ;(movement-demo/start-game)
    (logic/start-game)))