(ns clojuremistakes.game.engine
  (:require ["pixi.js" :as pixi]))

(defn- add-to-all-indexes [id keys indexes]
  (let [kvs (interleave keys (repeat #{id}))
        add-indexes (apply hash-map kvs)]
    (merge-with into indexes add-indexes)))


(def id (atom 0))
(defn next-id []
  (swap! id inc))


(defn register-go "state looks like 
                   {
                   :gos #{} 
                   :indexes {:x #{} :y #{}} 
                   }"
  [{:keys [id keys] :as go} state]
  (let [id (if nil? (next-id) id)]
    (swap! state (fn [state]
                   (let [state (assoc-in state [(:gos state) id] go)
                         indexes (add-to-all-indexes id keys (:indexes state))]
                     (assoc state :indexes indexes))))))






(comment
  (add-to-all-indexes 2 #{:player :visible} {:enemy #{1}, :visible #{1}}))