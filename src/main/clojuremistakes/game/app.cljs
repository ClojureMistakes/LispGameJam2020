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
                        :total-elapsed-time 0})))



; (defn game-loop [game]
;   (let []
;     (js/requestAnimationFrame
;      (fn [ts]
;        (let [ts (* ts 0.001)]
;          (.clear (:graphics @state))
;          (draw-circle {:radius (* 3 ts) :x (* 2 ts) :y (* 2 ts) :backgroundColor (.toString (:total-time game) 16)})
;          (game-loop (assoc game
;                            :delta-time (- ts (:total-time game))
;                            :total-time ts)))))))

(defn init-game-loop [on-tick]
  (println (:app @state))
  (.add (.-ticker (:app @state)) on-tick))

(defonce my-persistent-circle (atom {:x 10
                                     :y 10
                                     :radius 10
                                     :backgroundColor 0x87ceeb}))

(defonce my-list-of-circles (list my-list-of-circles))

(defn add-circle-to-list [circles]
  (cons (atom (assoc default-circle :x (rand-int width) :y (rand-int height))) circles))

(defn draw-circle [circle]
  (let [{:keys [x
                y
                radius
                borderWidth
                borderColor
                borderAlpha
                backgroundColor]} (merge default-circle @circle)
        {:keys [graphics]} @state]
    ; (println (str "Drawing circle " @circle))
    (.lineStyle graphics borderWidth borderColor borderAlpha)
    (.beginFill graphics backgroundColor)
    (.drawCircle graphics x y radius)
    (.endFill graphics)))

(defn resize-circle [delta circle]
  (let [{:keys [total-elapsed-time]} @state
        elapsed-time-in-seconds (Math/round (/ total-elapsed-time 100))]
    (swap! circle assoc
           :radius (cond
                     (= 0 (mod elapsed-time-in-seconds 7)) (:radius default-circle)
                     :else (+ (/ delta 10) (:radius @circle)))
           :borderWidth (cond
                          (= 0 (mod elapsed-time-in-seconds 7)) (:borderWidth default-circle)
                          :else (+ (/ delta 30) (:borderWidth @circle))))))

(defn move-circle [delta circle]
  (let [{:keys [total-elapsed-time]} @state
        elapsed-time-in-seconds (Math/round (/ total-elapsed-time 100))]
    (swap! circle assoc
           :x (cond
                ; (= 0 (mod elapsed-time-in-seconds 7)) (:x default-circle)
                (even? elapsed-time-in-seconds) (+ delta (:x @circle))
                (= 0 (mod elapsed-time-in-seconds 3)) (+ (* 3 (rand-int delta)) (:x @circle))
                :else (- (:x @circle) delta))
           :y (cond
                ; (= 0 (mod elapsed-time-in-seconds 7)) (:y default-circle)
                (odd? elapsed-time-in-seconds) (+ delta (:y @circle))
                (= 0 (mod elapsed-time-in-seconds 3)) (+ (* 3 (rand-int delta)) (:y @circle))
                :else (- (:y @circle) delta)))))

(defn init []
  (let [{:keys [graphics
                app]} @state]
    (println "Starting game...")
    (println "Adding shared grapics object to PIXI.Stage")
    (.addChild (.-stage app) graphics)
    (println "Initializing game loop")
    (add-circle-to-list my-list-of-circles)
    (init-game-loop
     (fn [delta]
       (.clear (:graphics @state))
       (doseq [circle  (add-circle-to-list  (add-circle-to-list  (add-circle-to-list  (add-circle-to-list my-list-of-circles))))]
         (move-circle delta circle)
         (resize-circle delta circle)
         (draw-circle circle))
       (swap! state assoc :total-elapsed-time (+ delta (:total-elapsed-time @state)))))))