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
    8 :backspace
    9 :tab
    13 :enter
    16 :shift
    17 :ctrl
    18 :alt
    19 :pause-break
    20 :caps-lock
    27 :escape
    33 :page-up
    32 :space
    34 :page-down
    35 :end
    36 :home
    37 :arrow-left
    38 :arrow-up
    39 :arrow-right
    40 :arrow-down
    44 :print-screen
    45 :insert
    46 :delete
    48 :0
    49 :1
    50 :2
    51 :3
    52 :4
    53 :5
    54 :6
    55 :7
    56 :8
    57 :9
    65 :a
    66 :b
    67 :c
    68 :d
    69 :e
    70 :f
    71 :g
    72 :h
    73 :i
    74 :j
    75 :k
    76 :l
    77 :m
    78 :n
    79 :o
    80 :p
    81 :q
    82 :r
    83 :s
    84 :t
    85 :u
    86 :v
    87 :w
    88 :x
    89 :y
    90 :z
    91 :left-window-key
    92 :right-window-key
    93 :select-key
    96 :numpad-0
    97 :numpad-1
    98 :numpad-2
    99 :numpad-3
    100 :numpad-4
    101 :numpad-5
    102 :numpad-6
    103 :numpad-7
    104 :numpad-8
    105 :numpad-9
    106 :multiply
    107 :add
    109 :subtract
    110 :decimal-point
    111 :divide
    112 :f1
    113 :f2
    114 :f3
    115 :f4
    116 :f5
    117 :f6
    118 :f7
    119 :f8
    120 :f9
    121 :f10
    122 :f11
    123 :f12
    144 :num-lock
    145 :scroll-lock
    186 :semicolon
    187 :equal-sign
    188 :comma
    189 :dash
    190 :period
    191 :forward-slash
    219 :open-bracket
    220 :back-slash
    221 :close-braket
    222 :single-quote
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
(defn listen-for-resize [on-resize]
  (events/listen js/window "resize"
                 (fn [event]
                   (on-resize))))