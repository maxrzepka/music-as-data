# music-as-data

## Principle

Give a pure data representation of music

Transcript a simple overtone example [funk.clj](https://github.com/overtone/overtone/blob/master/src/overtone/examples/getting_started/funk.clj)
into plain clj datastructure

This original code in overtone

```clojure
(defn drums [nome]
    (let [beat (nome)]
        ; hi-hat pattern
        (at (nome beat) (close-hihat))
        (at (nome (+ 1 beat)) (open-hihat))
        (at (nome (+ 2 beat)) (close-hihat))
        (at (nome (+ 3 beat)) (close-hihat))
        (at (nome (+ 4 beat)) (close-hihat))
        (at (nome (+ 5 beat)) (open-hihat))
        (at (nome (+ 6 beat)) (close-hihat))
        (at (nome (+ 7 beat)) (close-hihat))

        ; snare pattern
        (at (nome (+ 2 beat)) (snare))
        (at (subdivide (nome (+ 2 beat)) (nome (+ 4 beat)) 3) (snare))
        (at (subdivide (nome (+ 4 beat)) (nome (+ 6 beat)) 1) (snare))
        (at (nome (+ 6 beat)) (snare))
        (at (subdivide (nome (+ 6 beat)) (nome (+ 8 beat)) 3) (snare))

        ; kick drum pattern
        (at (nome beat) (kick))
        (at (nome (+ 5 beat)) (kick))
        (at (nome (+ 7 beat)) (kick))
        (apply-at (nome (+ 8 beat)) drums nome [])))

```

```clojure
[[:close-hihat 0 2 3 4 6 7]
 [:open-hihat 1 5]
 [:snare 2 7/2 9/2 6 15/2]
 [:kick 0 5 7]]
```

Other sound can be written like `[:freesound 1234]` `[:string 51]`

Then convert data into overtone music `(play (compose data) 120)` .

## Usage

To run the web app :

  - `lein repl`
  - start overtone server : `user=> (use '[overtone.core :as c]) (c/boot-internal-server))`
  - compile music-as-data.web
  - run server `(music-as-data.web/start port)`

## TODO

   - Righ-side Vertical Panel : list of all sounds available , add new one from freesound
   - Middle panel : lines of sound each line is decompose in 8 cells : checked cell = sound
   - Play current composition with live changes
   - Record sound to mp3 file.

## ISSUE

   - Find way to deploy/run overtone webapp.

## License

Copyright © 2012 Maximilien Rzepka

Distributed under the Eclipse Public License, the same as Clojure.
