(ns clojure-ttt.ui)

(defn prompt [message]
  (println message)
  (read-line))

(defn exit [status message]
  (println message)
  (System/exit status))

(defn print-board [board]
  (let [size (int (Math/sqrt  (count board)))]
  (->> (partition size board)
       (map #(apply str %))
       (map #(clojure.string/join " " %))
       (map println)
       (dorun))
  (print (str "----------------\n"))))



;print size -1 rows "___|___|
;on space where size = space in the row replace | with /n for all rows
;___|___|___
;___|___|___
;on row where = size replace _ with ""

(defn convert-row-to-board [row size]
  (let [last-space (nth row (dec size))
        other-spaces (pop row)]
     (apply str
      (apply str (map #(if (string? %) (str "_" % "_|") "___|") other-spaces))
      (if (string? last-space) (str "_" last-space "_\n") "___\n"))))


(defn convert-last-row-to-board [last-row size]
  (let [last-space (nth last-row (dec size))
        other-spaces (pop last-row)]
    (apply str
      (apply str (map #(if (string? %) (str " " % " |") "   |") other-spaces))
      (if (string? last-space) (str " " last-space " \n") "   \n"))))


(defn convert-board [board]
  (let [size (int (Math/sqrt (count board)))
        rows  (vec (partition size board))
        last-row (vec (nth rows (dec size)))
        other-rows (map #(vec %) (pop rows))
        converted-other-rows (apply str (map #(convert-row-to-board % size) other-rows))
        converted-last-row (convert-last-row-to-board last-row size)]
    (apply str converted-other-rows converted-last-row)))

(defn print-board2 [board]
 (print (convert-board board)))



(defn invalid-move []
  (println "Invalid move, please choose a valid space to move to"))

(def cli-options
  [["-f" "--first MARKER" "Player1 marker"
    :id :player1
    :default "X"
    :validate [#(= (count %) 1) "Marker is too long"]]
   ["-s" "--second MARKER" "Player2 marker"
    :id :player2
    :default "O"]
   ["-d" "--difficulty LEVEL" "AI difficulty"
    :default 3
    :desc "1 = dumb AI, 3=  AI wins 85% of time, 8= unbeatable AI"
    :parse-fn #(Integer. %)]
   ["-b"  "--board SIZE" "board size"
    :default 3
    :parse-fn #(Integer. %)]
   ["-h"  "--help"]])

(defn choose-space [players]
  (let [current-player (:marker (first players))]
  (prompt (str "It is your turn, " current-player ". Choose an unmarked space."))))

(defn human-make-move [board players]
  (try (Integer. (choose-space players))
    (catch Exception e (do
                         (print (str (.getMessage e) " That's not a number. Let's try that again!\n"))
                         (human-make-move board players)))))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn usage [options-summary]
  (->>["Welcome to Clojure TicTacToe."
       ""
       "Usage: lein run action [options]"
       ""
       "Examples:"
       "  lein run me-first -f x -s o      comp v. human, human goes first"
       "  lein run head-to-head -f q -s H -b 4    human v. human on a 4x4 board"
       ""
       "Options Summary:"
       options-summary
       ""
       "Actions:"
       "  me-first       play first against computer"
       "  comp-first     play second against the computer"
       "  head-to-head   play against another human"]
    (clojure.string/join \newline)))

(defn validate-cli [options arguments summary errors]
  (cond
    (:help options) (exit 0 (usage summary))
    (not= (count arguments) 1) (exit 1 (usage summary))
    (= (:player1 options) (:player2 options)) (exit 1 "Markers cannot be the same.")
    errors (exit 1 (error-msg errors))
    (not (some #(= (first arguments) %) ["me-first" "comp-first" "head-to-head"])) (exit 1 (usage summary))))



(defn winner [players]
  (print (str "Game over! " (:marker (second players)) " wins!\n")))

(defn tie []
  (print (str "Game over! It's a tie!\n")))
