(ns clojuremistakes.game.app
  (:require
   [clojuremistakes.game.circle :as circle]
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
                        :fps-text (pixi/Text. "")
                        :delta 0
                        ; vector of circles, initialized with one circ in the center
                        :circles [{:x (/ width 2)
                                   :y (/ height 2)
                                   :radius 10
                                   :backgroundColor 0x87ceeb}]})))

(def init-fps
  (let [app  (:app @state)
        fps  (:fps-text @state)]
    (set! (.-x fps) 0)
    (set! (.-y fps) 0)
    (.addChild (.-stage app) fps)))

(defn draw-circle [circle]
  (let [{:keys [x y radius borderWidth borderColor borderAlpha backgroundColor]} (merge default-circle circle)
        {:keys [graphics]} @state]
    (.lineStyle graphics borderWidth borderColor borderAlpha)
    (.beginFill graphics backgroundColor)
    (.drawCircle graphics x y radius)
    (.endFill graphics)))

(defn init-game-loop [on-tick]
  (.add (.-ticker (:app @state)) on-tick))

(defn draw-fps []
  (set! (.-text (:fps-text @state)) (.-FPS (.-shared pixi/Ticker))))

(defn render-game [delta]
  (doseq [circle (:circles @state)]
    (draw-circle circle))
  (draw-fps)
  #_(println (str "Drawing circle at " (:x @my-persistent-circle) "," (:y @my-persistent-circle))))

(defn random-color []
  (str "0x" (.toString (rand-int 255)  16) (.toString  (rand-int 255) 16) (.toString  (rand-int 255) 16)))

(defn add-circle []
  (swap! state assoc :circles (conj (:circles @state) {:x (rand-int width) :y (rand-int height) :backgroundColor (random-color)})))

(defn init []
  (let [{:keys [graphics
                app]} @state]
    (println "Starting game...")
    (println "Adding shared grapics object to PIXI.Stage")
    (.addChild (.-stage app) graphics)
    (println "Initializing game loop")
    (doseq [n (range 10)] (add-circle))
    (init-game-loop
     (fn [delta]
       (swap! state assoc :delta delta)
       (.clear (:graphics @state))
       (swap! state (fn [curr-state]
                      (let [state curr-state
                            total (:total-elapsed-time state)
                            circles (:circles state)
                            circles (map (fn [circle] (circle/move state circle)) circles)
                            circles (map (fn [circle] (circle/resize state circle)) circles)]
                        (assoc state :circles circles :total-elapsed-time (+ delta total)))))
       (render-game delta)))))


(comment

  (def entities [:container {:id 1}
                 [:circle {}
                  :circle {}]])
  ;; => #'clojuremistakes.game.app/entities


  (defn render-container [container & rest]
    ;check e pool for id, otherwise create new pixi container
    ;for each the rest and add them to container
    ;
    )

  (defn render-tree [tree]
    (let [el (first tree)
          props (second tree)
          r (drop 1 (rest tree))]
      (case el
        :container (do (render-container props r))
        :circle)))
  ;; => #'clojuremistakes.game.app/render-tree

  ;; => #'clojuremistakes.game.app/render-tree

  (render-tree entities)
  ;; => nil

  ;; => nil
  )