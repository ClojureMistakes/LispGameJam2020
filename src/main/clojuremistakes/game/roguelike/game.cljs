(ns clojuremistakes.game.roguelike.game
  (:require
   [clojuremistakes.game.input :as input]
   ["pixi.js" :as pixi]
   ["rot-js" :as rot]))


(defn get-width [app]
  (.-width (.-screen app)))

(defn get-height [app]
  (.-height (.-screen app)))

(defn create-digger-map []
  (let [c (new (.-Digger rot/Map) 200 100)]
    c))

(defonce state (atom (let [canvas (js/document.getElementById "game_canvas")
                           container (pixi/Container.)
                           app  (pixi/Application. #js {:autoResize true
                                                        :view canvas
                                                        :antialias true
                                                        :transparent false
                                                        :sharedTicker true
                                                        :backgroundColor 0x000000})]
                    ;;    (.addChild (.-stage app) container)
                    ;;    (.addChild (.-stage app) graphics)
                       (set! (.-backgroundColor (.-renderer app)) 0x000000)
                       (set! (.-autoStart (.-ticker app)) false)
                       {:canvas canvas
                        :app app
                        :container container
                        :total-elapsed-time 0
                        :delta 0
                        :fps-text (pixi/Text. "" #js {:fill 0xfff000})
                        :pressed-keys #{}
                        :mouse-x 0
                        :mouse-y 0
                        :px 0
                        :py 0
                        :map nil})))


(def init-fps
  (let [app  (:app @state)
        fps  (:fps-text @state)]
    (set! (.-x fps) 0)
    (set! (.-y fps) 0)
    (.addChild (.-stage app) fps)))

(defn init-game-loop [on-tick]
  (let [ticker (.-shared pixi/Ticker)]
    ;; (set! (.-maxFPS ticker) 1) ; set the speed of the ticker to 5 frames per second
    ;; (.stop ticker)
    ;; (.add ticker on-tick)
    ;; (.start ticker)
    ; the max fps flag isn't working so i just disabled the ticker and used set interval to manually cap FPS; 
    (.setInterval js/window (fn []
                              (on-tick)) 100)))

(defn draw-fps []
  (set! (.-text (:fps-text @state)) (Math/floor (.-FPS (.-shared pixi/Ticker))))
  (set! (.-x (.-position (:fps-text @state))) (- (get-width (:app @state)) 50))
  (set! (.-y (.-position (:fps-text @state))) (- (get-height (:app @state)) 40)))

(defn render-game []
  (draw-fps))

(defn handle-tick [delta]
  (swap! state assoc :delta delta)
  (swap! state (fn [curr-state]
                 (let [state curr-state
                       total (:total-elapsed-time state)]
                   (assoc state
                          :total-elapsed-time (+ delta total)))))
  (render-game))

(defn handle-resize [app]
  (.resize (.-renderer app) (.-innerWidth js/window) (.-innerHeight js/window)))

(defn start-game []
  (let [{:keys [canvas app map px py]} @state]
    ; Event Handling
    (events/listen js/window "keyup"
                   (fn [event]
                     (when-let
                      [k (input/keycode->keyword (.-keyCode event))]
                       (condp = k
                         :w (swap! state assoc :y (- 1 py))
                         :a (swap! state assoc :x (- 1 px))
                         :s (swap! state assoc :y (+ 1 py))
                         :d (swap! state assoc :y (+ 1 px))
                         nil)
                       (swap! state update :pressed-keys conj k))))
    (events/listen js/window "keyup"
                   (fn [event]
                     (when-let [k (input/keycode->keyword (.-keyCode event))]
                       (swap! state update :pressed-keys disj k))))
    (input/listen-for-resize
     (fn []
       (handle-resize app)
       (render-game)))
    ; Resize the application on start up
    (handle-resize app)
    ; Set up the game loop interval
    (init-game-loop handle-tick)))