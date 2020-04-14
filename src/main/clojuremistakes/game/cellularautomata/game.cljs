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
attribute vec2 walls;
uniform mat3 translationMatrix;
uniform mat3 projectionMatrix;

void main() {
    gl_PointSize = 5.0;
    gl_Position = vec4 ((projectionMatrix * translationMatrix * vec3 (walls.xy, 1.0)).xy, 0.0, 1.0);
}
")

(def fragmentSrc "
precision mediump float;

void main () {
    gl_FragColor = vec4 (1.0, 1.0, 0.0, 1.0);
}
")

(defn get-map-cells [rot-map]
  (.-_map rot-map))

(defn create-cellular-map []
  (let [c (new (.-Cellular rot/Map) 350 150 #js {:topology 8 :born #js [3] :survive #js [2,3]})]
    (.randomize c 0.3)
    (.create c)
    c))

(defn create-divided-maze-map []
  (let [c (new (.-DividedMaze rot/Map) 100 100)]
    c))

(defn create-icey-maze-map []
  (let [c (new (.-IceyMaze rot/Map) 100 100)]
    c))


; GL point-size is set to 5px currently, so in order to space apart the x/y coords so they don't overlap
; I multiply the x/y buffers by 5px to space them apart
(def scale-factor 5)

(defn transform-cell-map-js [cells]
  (let [buffer #js []]
    (.create cells
             (fn [x y wall]
               (when (= 1 wall)
                 (.push buffer #js [(* scale-factor x) (* scale-factor y)]))))
    #_(.log js/console buffer)
    (.flat buffer)))

; Converts 2D matrix of 0/1 values to 2D array of [[x,y,life]]
; [ [0, 1, 0, 0] ] -> [ [ 0, 0, 0], [1, 0, 1] ...]
(defn transform-cell-map [cells]
  (let [cells-as-vec (js->clj (get-map-cells cells))
        transformed-cells (for [y (range (count cells-as-vec))
                                x (range (count (first cells-as-vec)))
                                :when (= 1 (get (get cells-as-vec y) x))]

                            [x y])]
    (println transformed-cells)
    (clj->js (flatten transformed-cells))))

;; (defn stringify-cell-map [cells]
;;   (let [cells-as-vec (js->clj (get-map-cells cells))]
;;     (for [y (range (count cells-as-vec))
;;           x (range (count (first cells-as-vec)))
;;           c (flatten cells-as-vec)]
;;       (if (= c 1) " " "#"))))

(defn create-mesh [cells]
  (let [geometry (pixi/Geometry.)
        shader (.from pixi/Shader vertexSrc fragmentSrc)
        cell-transform  (transform-cell-map-js cells)]
    (.addAttribute geometry "walls" cell-transform)
    (.log js/console cell-transform)

    (pixi/Mesh. geometry shader nil (.-POINTS pixi/DRAW_MODES))))

(defonce state (atom (let [canvas (js/document.getElementById "game_canvas")
                           container (pixi/Container.)
                           graphics (pixi/Graphics.)
                           map (create-cellular-map)
                           mesh (create-mesh map)
                           app  (pixi/Application. #js {:autoResize true
                                                        :view canvas
                                                        :antialias true
                                                        :transparent false
                                                        :sharedTicker true
                                                        :backgroundColor 0x000000})]
                    ;;    (.addChild (.-stage app) container)
                    ;;    (.addChild (.-stage app) graphics)
                    ;;    
                       (set! (.-backgroundColor (.-renderer app)) 0xff0000)
                       (.addChild (.-stage app) mesh)
                       (set! (.-autoStart (.-ticker app)) false)
                       {:graphics graphics
                        :canvas canvas
                        :app app
                        :mesh mesh
                        :map map
                        :container container
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
  (let [ticker (.-shared pixi/Ticker)]
    ;; (set! (.-maxFPS ticker) 1) ; set the speed of the ticker to 5 frames per second
    (.stop ticker)
    (.add ticker on-tick)
    (.start ticker)
    ; the max fps flag isn't working so i just disabled the ticker and used set interval to manually cap FPS; 
    #_(.setInterval js/window (fn []
                                (.update ticker)) 100)))

(defn draw-fps []
  (set! (.-text (:fps-text @state)) (Math/floor (.-FPS (.-shared pixi/Ticker))))
  (set! (.-x (.-position (:fps-text @state))) (- (get-width (:app @state)) 50))
  (set! (.-y (.-position (:fps-text @state))) (- (get-height (:app @state)) 40)))

(defn render-game []
  (draw-fps)
  ;(.render (.-renderer (:app @state)) (.-stage (:app @state))
  )

(defn update-map []
  (.create (:map @state)))

(defn update-mesh []
  (let [{:keys [map]} @state
        updated-walls (transform-cell-map-js map)
        buffer (.getBuffer (.-geometry (:mesh @state)) "walls")
        updated-buffer (.from pixi/Buffer updated-walls)] ; fetches current buffer with data as a Float32Array
    #_(.log js/console buffer)
    (.update buffer (.-data updated-buffer))))

(defn handle-tick [delta]
  (update-map)
  (update-mesh)
  (swap! state assoc :delta delta)
  (swap! state (fn [curr-state]
                 (let [state curr-state
                       total (:total-elapsed-time state)
                       mouse-x (:mouse-x state)
                       mouse-y (:mouse-y state)]
                   (assoc state
                          :total-elapsed-time (+ delta total)))))
;;   (update-map delta)
;;   (println (get-map-cells (:map @state)))
  (render-game))

(defn handle-resize [app]
  (.resize (.-renderer app) (.-innerWidth js/window) (.-innerHeight js/window)))

(defn start-game []
  (let [{:keys [graphics canvas app map]} @state]
    ; failed attempt to make new cells on click
    #_(input/listen-for-mousedown canvas
                                  (fn [mx my]
                                    (.log js/console mx my)
                                  ; perhaps make it so that it sets more than one individual pixel alive so it doesn't
                                  ; immediately die (must have neighbors)
                                    (for [dx (range 10)]
                                      (let [x (Math/floor (/ (+ dx mx) scale-factor))
                                            y (Math/floor (/ (+ dx my) scale-factor))]
                                        (.log js/console mx my)
                                        (aset y x map 1)
                                    ; this doesn't work after the initial create :(
                                        #_(.set (:map @state)   1)))))
    (input/listen-for-resize
     (fn []
       (handle-resize app)
       (render-game)))
    ;; (println "Properly resize PIXI Application on start-up")
    (handle-resize app)
    ;; (swap! state assoc :map (new (.-Cellular rot/Map)  (get-width app) (get-map-cells app)))
    (println "Initializing game loop")
    (.log js/console (:map @state))
    (init-game-loop handle-tick)))