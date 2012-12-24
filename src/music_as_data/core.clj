(ns music-as-data.core
  (:require [overtone.core :as c]))

(def close-hihat (c/sample (c/freesound-path 802)))
(def snare (c/sample (c/freesound-path 26903)))
(def kick (c/sample (c/freesound-path 2086)))
(def open-hihat (c/sample (c/freesound-path 26657)))

(def met (c/metronome (* 100 2)))

;;
;;TODO handle any sound
;;
(defmacro compose
  " returns a function playing the defined arrangement
usage (compose [[:close-hihat 0 2 3 4 6 7]
              [:open-hihat 1 5]
              [:snare 2 7/2 9/2 6 15/2]])
"
  [data]
  (let [nome (gensym "nome")
        beat (gensym "beat")
        compose (gensym "compose")]
    `(fn ~compose [~nome]
            (let [~beat (~nome)]
              ~@(for [[sound & times] data t times]
                  `(c/at (~nome (+ ~t ~beat)) (~(symbol (name sound)))))
              (c/apply-at (~nome (+ 8 ~beat)) ~compose ~nome [])))))
