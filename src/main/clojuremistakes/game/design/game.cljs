(ns clojuremistakes.game.design.game)

;in the engine namespace
(def id (atom 0))
(defn next-id []
  (swap! id inc))

;in the player namespace...
(defn player-game-logic [state id]
  (swap! state (fn [{:keys [x y] :as state}] (assoc state :x y :y x))))



(defn player-render [state])

(defn create-player-go []
  {:player {:x 200 :y 400
            :id (next-id)
            :tags #{:player :figher :hero :visible}
            :game-logic player-game-logic
            :render player-render}})



;in the enemy namespace...
(defn enemy-game-logic [state id]
  (swap! state (fn [{:keys [x y] :as state}] (assoc state :x y :y x))))

(defn enemy-render [state])

(defn create-enemy-go []
  {:enemy {:x 200 :y 400
           :id (next-id)
           :game-logic enemy-game-logic
           :render enemy-render}})


;in the ...engine namespace?
;
;
;5

(def state (atom {:go [(create-player-go)
                       (create-player-go)
                       (create-enemy-go)
                       (create-enemy-go)
                       (create-player-go)]
                  :indexes {:player [2 8]
                            :hero [1]}}))

(defn run-game-logic [state]
  (let [game-objects (:go @state)]
    (doall (map (fn [{:keys [go id]}]
                  ((:game-logic go) state id)) game-objects))))