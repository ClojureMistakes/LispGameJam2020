(ns clojuremistakes.game.utils)

(defn random-color []
  (str "0x" (.toString (rand-int 255)  16) (.toString  (rand-int 255) 16) (.toString  (rand-int 255) 16)))