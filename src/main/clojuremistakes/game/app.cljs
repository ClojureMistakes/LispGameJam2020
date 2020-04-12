(ns clojuremistakes.game.app
  (:require
   [clojuremistakes.game.input :as input]
   [clojuremistakes.game.game :as game]
   [clojuremistakes.game.pixi :as pixi-utils]
   ["pixi.js" :as pixi]))

(def width 800)
(def height 600)
(def hex-color-max 16777216)
(defonce default-circle {:x (/ width 2)
                         :y (/ height 2)
                         :radius 10
                         :borderWidth 1
                         :borderColor 0xff0000
                         :borderAlpha 1
                         :backgroundColor 0xff0000})

(defonce state (atom (let [canvas (js/document.getElementById "game_canvas")
                           app (pixi-utils/pixi-application width height canvas)]
                       {:graphics (pixi/Graphics.)
                        :canvas canvas
                        :app app
                        :container (pixi-utils/create-container app)
                        :total-elapsed-time 0
                        :fps-text (pixi/Text. "")})))

(def init-fps
  (let [app  (:app @state)
        fps  (:fps-text @state)]
    (set! (.-x fps) 0)
    (set! (.-y fps) 0)
    (.addChild (.-stage app) fps)))

(defn draw-circle [circle]
  (let [{:keys [x
                y
                radius
                borderWidth
                borderColor
                borderAlpha
                backgroundColor]} (merge default-circle circle)
        {:keys [graphics
                container]} @state]
    (.lineStyle graphics borderWidth borderColor borderAlpha)
    (.beginFill graphics backgroundColor)
    (.drawCircle graphics x y radius)
    (.endFill graphics)))

#_(defn game-loop [game]
    (let []
      (js/requestAnimationFrame
       (fn [ts]
         (let [ts (* ts 0.001)]
           (.clear (:graphics @state))
           (draw-circle {:radius (* 3 ts) :x (* 2 ts) :y (* 2 ts) :backgroundColor (.toString (:total-time game) 16)})
           (game-loop (assoc game
                             :delta-time (- ts (:total-time game))
                             :total-time ts)))))))

(defn init-game-loop [on-tick]
  (.add (.-ticker (:app @state)) on-tick))

(defonce my-persistent-circle (atom {:x (/ width 2)
                                     :y (/ height 2)
                                     :radius 10
                                     :backgroundColor 0x87ceeb}))

(defn move-circle [delta]
  (let [{:keys [total-elapsed-time]} @state
        elapsed-time-in-seconds (Math/round (/ total-elapsed-time 100))]
    (swap! my-persistent-circle assoc
           :radius (cond
                     (= 0 (mod elapsed-time-in-seconds 7)) (:radius default-circle)
                     :else (+ (/ delta 10) (:radius @my-persistent-circle)))
           :borderWidth (cond
                          (= 0 (mod elapsed-time-in-seconds 7)) (:borderWidth default-circle)
                          :else (+ (/ delta 10) (:borderWidth @my-persistent-circle)))
           :x (cond
                (= 0 (mod elapsed-time-in-seconds 7)) (:x default-circle)
                (even? elapsed-time-in-seconds) (+ delta (:x @my-persistent-circle))
                (= 0 (mod elapsed-time-in-seconds 3)) (+ (* 3 (rand-int delta)) (:x @my-persistent-circle))
                :else (- (:x @my-persistent-circle) delta))
           :y (cond
                (= 0 (mod elapsed-time-in-seconds 7)) (:y default-circle)
                (odd? elapsed-time-in-seconds) (+ delta (:y @my-persistent-circle))
                (= 0 (mod elapsed-time-in-seconds 3)) (+ (* 3 (rand-int delta)) (:y @my-persistent-circle))
                :else (- (:y @my-persistent-circle) delta)))))

(defn draw-fps []
  (set! (.-text (:fps-text @state)) (.-FPS (.-shared pixi/Ticker))))

(defn render-game [delta]
  (draw-circle @my-persistent-circle)
  (draw-fps)
  #_(println (str "Drawing circle at " (:x @my-persistent-circle) "," (:y @my-persistent-circle))))

(defn init []
  (let [{:keys [graphics
                app]} @state]
    (println "Starting game...")
    (println "Adding shared grapics object to PIXI.Stage")
    (.addChild (.-stage app) graphics)
    (println "Initializing game loop")
    (init-game-loop
     (fn [delta]
       (.clear (:graphics @state))
       (move-circle delta)
       (render-game delta)
       (swap! state assoc :total-elapsed-time (+ delta (:total-elapsed-time @state)))))))