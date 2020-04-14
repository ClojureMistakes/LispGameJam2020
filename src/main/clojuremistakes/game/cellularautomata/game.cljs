(ns clojuremistakes.game.cellularautomata.game
  (:require
   [clojuremistakes.game.input :as input]
   ["pixi.js" :as pixi]
   ["rot-js" :as rot]))

(defn get-width [app]
  (.-width (.-screen app)))

(defn get-height [app]
  (.-height (.-screen app)))


(defonce state (atom (let [canvas (js/document.getElementById "game_canvas")
                           container (pixi/Container.)
                           graphics (pixi/Graphics.)
                           app  (pixi/Application. #js {:autoResize true
                                                        :view canvas
                                                        :antialias true
                                                        :transparent false
                                                        :sharedTicker true
                                                        :backgroundColor 0x000000})]
                       (.addChild (.-stage app) container)
                       (.addChild (.-stage app) graphics)
                       {:graphics graphics
                        :canvas canvas
                        :app app
                        :container container
                        :map nil
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
  (set! (.-x (.-position (:fps-text @state))) (- (get-width) 50))
  (set! (.-y (.-position (:fps-text @state))) (- (get-height) 40)))

(defn render-game [delta]
  (draw-fps))


(defn get-map-cells [rot-map]
  (.-_map rot-map))

(defn update-map [delta]
  (let [cells (.-_map (:map @state))]
  (swap! state assoc :delta delta)
    
    )
  )

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
    (swap! state assoc :map (rot/Map/Cellular. (get-width app) (get-height app)))
    (println "Initializing game loop")
    (init-game-loop handle-tick)))