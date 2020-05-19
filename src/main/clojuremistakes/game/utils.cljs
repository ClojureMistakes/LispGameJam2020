(ns clojuremistakes.game.utils
  (:require ["pixi.js" :as pixi]
            [clojuremistakes.game.engine :as e]))

(defn random-color []
  (str "0x" (.toString (rand-int 255)  16) (.toString  (rand-int 255) 16) (.toString  (rand-int 255) 16)))


(defn fps-draw [this]
  (set! (.-text this) (Math/floor (.-FPS (.-shared pixi/Ticker)))))


(defn create-fps-go [app container state]
  (let [fps  (pixi/Text. "" #js {:fill 0xff0000})]
    (set! (.-x (.-position fps)) (- (get-width app) 50))
    (set! (.-y (.-position fps)) (- (get-height app) 40))
    (.addChild container fps)
    (e/register-go {:this fps
                    :draw fps-draw
                    :logic identity} state)))

