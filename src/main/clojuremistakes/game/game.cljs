(ns clojuremistakes.game.game
  (:require
   [play-cljc.gl.core :as pc-core]
   [play-cljc.gl.entities-2d :as entity]
   [play-cljc.macros-js :refer-macros [gl math]]))

(defonce *state (atom {:mouse-x 0
                       :mouse-y 0
                       :pressed-keys #{}
                       :camera (entity/->camera true)}))


(defn get-width [game]
  (-> game :context .-canvas .-clientWidth))

(defn get-height [game]
  (-> game :context .-canvas .-clientHeight))

(def background-image
  {:viewport {:x 0 :y 0 :width 0 :height 0}
   :clear {:color [(/ 150 255) (/ 0 255) (/ 200 255) 1] :depth 1}})

; Given the game state, returns a new game state based on the next tick
(defn tick [game]
  (let [{:keys [pressed-keys camera] :as state} @*state
        game-width (get-width game)
        game-height (get-height game)]
    (when (and (pos? game-width) (pos? game-height))
  ; move entities (e.g turn engine)
  ; render the game
  ; render background
      (pc-core/render game (update background-image :viewport assoc :width game-width :height game-width)))
  ; render entities
    ))

(defn init [game]
  (gl game enable (gl game BLEND))
  (gl game blendFunc (gl game SRC_ALPHA) (gl game ONE_MINUS_SRC_ALPHA))
  ; load assets if we have any
  )