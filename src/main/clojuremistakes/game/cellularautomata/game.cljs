(ns clojuremistakes.game.cellularautomata.game
  (:require
   [clojuremistakes.game.input :as input]
   ["pixi.js" :as pixi]
   ["rot-js" :as rot]))

(defn get-width [app]
  (.-width (.-screen app)))

(defn get-height [app]
  (.-height (.-screen app)))

(def vertexSrc "
precision mediump float;
attribute vec3 cells;
uniform mat3 translationMatrix;
uniform mat3 projectionMatrix;

void main() {
    gl_Position = vec4 ((projectionMatrix * translationMatrix * vec3 (cells, 1.0)) .xy, 0.0, 1.0);
}
")

(def fragmentSrc "
precision mediump float;

void main () {
    gl_FragColor = vec4 (1.0, 0.0, 0.0, 1.0);
}
")

(defn get-map-cells [rot-map]
  (.-_map rot-map))

(defn create-rot-map []
  (let [c (new (.-Cellular rot/Map) 10 10)]
    (.randomize c 0.5)
    (.create c)
    c))

; Converts 2D matrix of 0/1 values to 2D array of [[x,y,life]]
; [ [0, 1, 0, 0] ] -> [ [ 0, 0, 0], [1, 0, 1] ...]
(defn transform-cell-map [cells]
  (let [cells-as-vec (js->clj (get-map-cells cells))]
    (for [y (range (count cells-as-vec))
          x (range (count (first cells-as-vec)))
          c (flatten cells-as-vec)]
      [x y c])))

;; (defn stringify-cell-map [cells]
;;   (let [cells-as-vec (js->clj (get-map-cells cells))]
;;     (for [y (range (count cells-as-vec))
;;           x (range (count (first cells-as-vec)))
;;           c (flatten cells-as-vec)]
;;       (if (= c 1) " " "#"))))

(defn create-mesh [cells]
  (let [geometry (pixi/Geometry.)
        shader (.from pixi/Shader vertexSrc fragmentSrc)
        cell-transform  (transform-cell-map cells)]
    (.addAttribute geometry "cells" cell-transform)
    (pixi/Mesh. geometry shader)))

(defonce state (atom (let [canvas (js/document.getElementById "game_canvas")
                           container (pixi/Container.)
                           graphics (pixi/Graphics.)
                           map (create-rot-map)
                           mesh (create-mesh (get-map-cells map))
                           app  (pixi/Application. #js {:autoResize true
                                                        :view canvas
                                                        :antialias true
                                                        :transparent false
                                                        :sharedTicker true
                                                        :backgroundColor 0x000000})]
                       (.addChild (.-stage app) container)
                       (.addChild (.-stage app) graphics)
                       (.set (.-position mesh) 0 0)
                       (.addChild (.-stage app) mesh)
                       {:graphics graphics
                        :canvas canvas
                        :app app
                        :container container
                        :map map
                        :total-elapsed-time 0
                        :delta 0
                        :fps-text (pixi/Text. "" #js {:fill 0xff0000})
                        :pressed-keys #{}
                        :mouse-x 0
                        :mouse-y 0})))


(def init-fps
  (let [app  (:app @state)
        fps  (:fps-text @state)]
    (set! (.-x fps) 0)
    (set! (.-y fps) 0)
    (.addChild (.-stage app) fps)))

(defn init-game-loop [on-tick]
  (.add (.-ticker (:app @state)) on-tick))

(defn draw-fps []
  (set! (.-text (:fps-text @state)) (Math/floor (.-FPS (.-shared pixi/Ticker))))
  (set! (.-x (.-position (:fps-text @state))) (- (get-width (:app @state)) 50))
  (set! (.-y (.-position (:fps-text @state))) (- (get-height (:app @state)) 40)))

(defn render-game [delta]
  (draw-fps))




(defn update-map [delta]
  (.create (:map @state)))

(defn handle-tick [delta]
  (swap! state assoc :delta delta)
  (.clear (:graphics @state))
  (swap! state (fn [curr-state]
                 (let [state curr-state
                       total (:total-elapsed-time state)
                       mouse-x (:mouse-x state)
                       mouse-y (:mouse-y state)]
                   (assoc state
                          :total-elapsed-time (+ delta total)))))
;;   (update-map delta)
;;   (println (get-map-cells (:map @state)))
  (render-game delta))

(defn handle-resize [app]
  (.resize (.-renderer app) (.-innerWidth js/window) (.-innerHeight js/window)))

(defn start-game []
  (let [{:keys [graphics canvas app]} @state]
    (input/listen-for-mouse state canvas)
    (input/listen-for-resize
     (fn []
       (handle-resize app)
       (render-game 0)))
    (println "Properly resize PIXI Application on start-up")
    (handle-resize app)
    ;; (swap! state assoc :map (new (.-Cellular rot/Map)  (get-width app) (get-map-cells app)))
    (println "Initializing game loop")
    (init-game-loop handle-tick)))