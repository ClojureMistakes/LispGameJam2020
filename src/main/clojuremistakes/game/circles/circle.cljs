(ns clojuremistakes.game.circles.circle)

(defn resize [{:keys [delta total-elapsed-time]} {:keys [radius borderWidth] :as circle}]
  (let [elapsed-time-in-seconds (Math/round (/ total-elapsed-time 100))
        result  (assoc circle
                       :radius (cond
                                 (<= 5 (mod elapsed-time-in-seconds 10))
                                 (- radius (/ delta 10))
                                 :else
                                 (+ (/ delta 10)  radius))
                       :borderWidth (cond
                                      (<= 5 (mod elapsed-time-in-seconds 10))  (+ (/ delta 50)  borderWidth)
                                      :else (- borderWidth (/ delta 50))))]
    #_(println (str "resize: " result))
    result))


(defn move [{:keys [delta total-elapsed-time]} {:keys [x y] :as circle}]
  (let [elapsed-time-in-seconds (Math/round (/ total-elapsed-time 100))
        result (assoc circle
                      :x (cond
                           (even? elapsed-time-in-seconds) (+ delta x (rand-int delta))
                           :else (-  x delta  (rand-int delta)))
                      :y (cond
                           (even? elapsed-time-in-seconds)
                           (+ delta y (rand-int delta))
                           :else
                           (- y delta  (rand-int delta))))]
    #_(println (str "move: " result))
    result))
