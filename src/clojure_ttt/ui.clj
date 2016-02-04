(ns clojure-ttt.ui)

(defprotocol IO
  (print-io [this anything])
  (read-io [this]))

(deftype ConsoleIO [ ]
  IO
  (print-io [this anything]
    (println anything))
  (read-io [this]
    (read-line)))

(defn new-console-io [ ]
  (ConsoleIO.))

(defn prompt [io something]
  (print-io io something)
  (read-io io))

(defn display-board [board io board-display]
  (print-io io (str "\n"(board-display board))))

(defn choose-space [markers io]
  (let [current-marker (first markers)]
    (prompt io (str "It is your turn, " current-marker ". Choose an unmarked space."))))

(defn validate-move [io board move]
 (let [available-moves (filter number? board)]
   (if (not (some #(= (Integer. move) %) available-moves))
     (validate-move io board (prompt io "Invalid move, please choose a valid space to move to"))
     (Integer. move))))

(defn human-make-move [board markers io board-display]
  (display-board board io board-display)
  (let [move (choose-space markers io)]
  (try (validate-move io board move)
    (catch Exception e (do
                         (print (str (.getMessage e) " That's not a number. Let's try that again!\n"))
                         (human-make-move board markers io board-display))))))



