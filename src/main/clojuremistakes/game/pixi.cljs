(ns clojuremistakes.game.pixi
  (:require ["pixi.js" :as pixi]))

(defn pixi-application [width height canvas]
  (pixi/Application. #js {:width width
                          :height height
                          :view canvas
                          :antialias true
                          :sharedTicker true}))

(defn create-container [app]
  (let [container (pixi/Container.)]
    (.addChild (.-stage app) container)
    container))