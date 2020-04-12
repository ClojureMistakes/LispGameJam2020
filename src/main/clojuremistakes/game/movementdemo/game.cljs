(ns clojuremistakes.game.movementdemo.game
  (:require
   [clojuremistakes.game.input :as input]
   [clojuremistakes.game.pixi :as pixi-utils]
   ["pixi.js" :as pixi]))

(def width 800)
(def height 600)

(defonce state (atom (let [canvas (js/document.getElementById "game_canvas")
                           app (pixi-utils/pixi-application width height canvas)]
                       {:graphics (pixi/Graphics.)
                        :canvas canvas
                        :app app
                        :container (pixi-utils/create-container app)
                        :total-elapsed-time 0
                        :delta 0
                        :fps-text (pixi/Text. "")
                        :pressed-keys #{}
                        :mouse-x 0
                        :mouse-y 0
                        :circle {:x 0
                                 :y 0
                                 :radius 10
                                 :borderWidth 10
                                 :borderColor 0xff0000
                                 :borderAlpha 1
                                 :backgroundColor 0xff0000}})))

(def init-fps
  (let [app  (:app @state)
        fps  (:fps-text @state)]
    (set! (.-x fps) 0)
    (set! (.-y fps) 0)
    (.addChild (.-stage app) fps)))

(defn init-game-loop [on-tick]
  (.add (.-ticker (:app @state)) on-tick))

(defn draw-circle [circle]
  (let [{:keys [x y radius borderWidth borderColor borderAlpha backgroundColor]} circle
        {:keys [graphics]} @state]
    (.lineStyle graphics borderWidth borderColor borderAlpha)
    (.beginFill graphics backgroundColor)
    (.drawCircle graphics x y radius)
    (.endFill graphics)))

(defn draw-fps []
  (set! (.-text (:fps-text @state)) (.-FPS (.-shared pixi/Ticker))))

(defn render-game [delta]
  (draw-circle (:circle @state))
  (draw-fps))

(defn start-game []
  (let [{:keys [graphics canvas app]} @state]
    (println "Starting game...")
    (println "Adding shared grapics object to PIXI.Stage")
    (.addChild (.-stage app) graphics)
    (println "Adding Event Listeners")
    (input/listen-for-mouse state canvas)
    (println "Initializing game loop")
    (init-game-loop
     (fn [delta]
       (swap! state assoc :delta delta)
       (.clear (:graphics @state))
       (swap! state (fn [curr-state]
                      (let [state curr-state
                            total (:total-elapsed-time state)
                            mouse-x (:mouse-x state)
                            mouse-y (:mouse-y state)
                            circle (:circle state)]
                        (assoc state
                               :circle (assoc circle :x mouse-x :y mouse-y)
                               :total-elapsed-time (+ delta total)))))
       (render-game delta)))))