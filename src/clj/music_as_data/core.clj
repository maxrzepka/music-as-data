(ns music-as-data.core
  (:require [overtone.core :as c]))

;; WARNING connect to collider server before compiling
;; WARNING network connection needed to launch the REPL

;; (c/boot-internal-server)  / (c/kill-server)
(if (c/server-disconnected?)
  (c/boot-internal-server))

(defn run-server []
  (when (c/server-connected?) (c/kill-server))
  (c/boot-internal-server))

;; # Some Sounds
(def close-hihat (c/sample (c/freesound-path 802)))
(def snare (c/sample (c/freesound-path 26903)))
(def kick (c/sample (c/freesound-path 2086)))
(def open-hihat (c/sample (c/freesound-path 26657)))

(c/definst string [note 60 amp 1.0 dur 0.5 decay 30 coef 0.3 gate 1]
  (let [freq (c/midicps note)
        noize (* 0.8 (c/white-noise))
        dly   (/ 1.0 freq)
        plk   (c/pluck noize gate dly dly decay coef)
        dist  (c/distort plk)
        filt  (c/rlpf dist (* 12 freq) 0.6)
        clp   (c/clip2 filt 0.8)
        reverb (c/free-verb clp 0.4 0.8 0.2)]
    (* amp (c/env-gen (c/perc 0.0001 dur) :action 0) reverb)))

(def met (c/metronome (* 100 2)))

(def sounds
  (atom {:close-hihat close-hihat
         :snare snare
         :kick kick
         :string string
         :open-hihat open-hihat}))

(def playlist
  {:funk
   [[:close-hihat 0 2 3 4 6 7]
    [:open-hihat 1 5]
    [:snare 2 7/2 9/2 6 15/2]
    [:kick 0 5 7]
    [:string [0 51] [1/2 51] [3/2 51] [5/2 51] [4 51] [9/2 49] [5 46] [6 51] [13/2 49] [7 46] [8 51] [12 51] [25/2 51]]]})

;;TODO write fct to validate composition

(defn guess-length
  "Guess length of line of composition : 4, 8, 16..."
  [line]
  (let [times (rest line)
        gtime (apply max (map #(if (sequential? %) (first %) %) times))
        ]
    (first (drop-while #(not (< gtime %))
                       (map #(int (Math/pow 4 %)) (range 1 10))))))

(defn note [n]
  (if (number? n) n (c/note n)))

(defn compose
  "Given a composition as data returns fct playing it with a given metronome
usage (compose [[:close-hihat 0 2 3 4 6 7]
              [:open-hihat 1 5]
              [:snare 2 7/2 9/2 6 15/2]])
"
  [data]
  (let [len (apply max (map guess-length data))]
    (fn play [nome]
            (let [beat (nome)]
              (doseq [[sound & times] data t times]
                (if (and (sequential? t) (> (count t) 1)) ;;case instrument
                  (c/at (nome (+ (first t) beat)) ((@sounds sound) (note (second t))))
                  (c/at (nome (+ t beat)) ((@sounds sound)))))
              (c/apply-at (nome (+ len beat)) play nome [])))))

(defn compose1 [line]
  (let [len (guess-length line)]
    (fn play [nome]
      (let [beat (nome)
            [sound & times] line]
        (doseq [t times]
                (if (and (sequential? t) (> (count t) 1)) ;;case instrument
                  (c/at (nome (+ (first t) beat)) ((@sounds sound) (note (second t))))
                  (c/at (nome (+ t beat)) ((@sounds sound)))))
        (c/apply-at (nome (+ len beat)) play nome [])))))

(defn play [data]
  (c/stop)
  ((compose data) met))

(defn play1 [data]
  (c/stop)
  (doseq [line data]
    ((compose1 line) met)))

(def stop c/stop)
;; (c/stop)