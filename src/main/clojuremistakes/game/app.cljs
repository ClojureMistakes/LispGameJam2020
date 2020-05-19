(ns clojuremistakes.game.app
  (:require
   #_[clojuremistakes.game.circles.game :as circles-demo]
   #_[clojuremistakes.game.mousemovementdemo.game :as movement-demo]
   #_[clojuremistakes.game.cellularautomata.game :as cellular-demo]
   [clojuremistakes.game.logic.game :as logic]
   ["pixi.js" :as pixi]
   ["pixi-text-input" :as pinput]
   [clojuremistakes.game.engine :as e]))



(defn create-pixi-application []
  (pixi/Application. #js {:width (.-innerWidth js/window)
                          :height (.-innerHeight js/window)
                          :view (js/document.getElementById "game_canvas")
                          :antialias true
                          :sharedTicker true
                          :transparent true}))

(def state (atom {:pixi-app (create-pixi-application)
                  :total-time 0
                  :time 0
                  :show-fps true
                  :gos {}}))

(defn init []
  (logic/start-game state))