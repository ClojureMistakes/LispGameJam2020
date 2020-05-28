(ns clojuremistakes.game.app
  (:require
   #_[clojuremistakes.game.circles.game :as circles-demo]
   #_[clojuremistakes.game.mousemovementdemo.game :as movement-demo]
   #_[clojuremistakes.game.cellularautomata.game :as cellular-demo]
   #_[clojuremistakes.game.logic.game :as logic]
   #_[clojuremistakes.game.engine :as e]
   [clojuremistakes.game.roguelike.game :as roguelike]
   ["pixi.js" :as pixi]
   ["pixi-text-input" :as pinput]))



;; (defn create-pixi-application []
;;   (pixi/Application. #js {:width (.-innerWidth js/window)
;;                           :height (.-innerHeight js/window)
;;                           :view (js/document.getElementById "game_canvas")
;;                           :antialias true
;;                           :sharedTicker true
;;                           :transparent true}))

;; (def state (atom {:pixi-app (create-pixi-application)
;;                   :total-time 0
;;                   :time 0
;;                   :show-fps true
;;                   :gos {}}))

(defn init []
  (roguelike/start-game))