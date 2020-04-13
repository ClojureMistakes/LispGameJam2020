(ns clojuremistakes.game.input
  (:require  [goog.events :as events]))

; On mouse move, update the game state's mouse-x and mouse-y
(defn listen-for-mouse [state canvas]
  (events/listen js/window "mousemove"
                 (fn [event]
                   (swap! state
                          (fn [state]
                            (let [bounds (.getBoundingClientRect canvas)
                                  x (- (.-clientX event) (.-left bounds))
                                  y (- (.-clientY event) (.-top bounds))]
                              (assoc state :mouse-x x :mouse-y y)))))))

(defn keycode->keyword [keycode]
  (condp = keycode
    37 :left
    39 :right
    38 :up
    nil))

; On key events, track current keydown/keyup keys in game state
(defn listen-for-keys [state]
  (events/listen js/window "keydown"
                 (fn [event]
                   (when-let [k (keycode->keyword (.-keyCode event))]
                     (swap! state update :pressed-keys conj k))))
  (events/listen js/window "keyup"
                 (fn [event]
                   (when-let [k (keycode->keyword (.-keyCode event))]
                     (swap! state update :pressed-keys disj k)))))

; On resize, set the canvas width / height to match that of the client window
#_(defn resize [context]
    (let [display-width context.canvas.clientWidth
          display-height context.canvas.clientHeight]
      (set! context.canvas.width display-width)
      (set! context.canvas.height display-height)))

; On resize, tell the PIXI Application renderer to resize to the window innerWidth & innerHeight
(defn listen-for-resize [app]
  (events/listen js/window "resize"
                 (fn [event]
                   (.resize (.-renderer app) (.-innerWidth js/window) (.-innerHeight js/window)))))