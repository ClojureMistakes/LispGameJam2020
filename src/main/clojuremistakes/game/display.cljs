(ns clojuremistakes.game.display
  (:require ["pixi.js" :as pixi]))

; width, height -> game display map 
; given a width and height, produce a game display object.
; game display caches the texture sprites for re-use, maintains track
; of the currently moving game sprites that are in the app stage
; and updates their x/y coordinates in the game display
(defn create-game-display [width height]
  (let [canvas (js/document.getElementById "game_canvas")
        app (pixi/Application. #js {:autoResize true
                                    :view canvas
                                    :width width
                                    :height height
                                    :antialias true
                                    :transparent false
                                    :sharedTicker true
                                    :backgroundColor 0x2a5250})]
    {:app app :width width :height height :texture-atlas {} :moving-sprites []}))

(defn load-and-generate-textures [display])

()


