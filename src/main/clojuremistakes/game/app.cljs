(ns clojuremistakes.game.app
  (:require
   [clojuremistakes.game.circles.game :as circles-demo]
   [clojuremistakes.game.movementdemo.game :as movement-demo]
   [clojuremistakes.game.pixi :as pixi-utils]
   ["pixi.js" :as pixi]))

(defn init []
  (let []
    ; Start the circles-demo
    ;(circles-demo/start-game)
    (movement-demo/start-game)))


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