(ns flexiana.backend.scramble
  (:require [clojure.set :as set]
            [clojure.data :as data]))

(defn scramble? [puzzle picture]
  (let [histogram-puzzle (frequencies puzzle)
        histogram-picture (frequencies picture)]
    (-> (for [y (keys histogram-picture)]
          (<= (get histogram-picture y 0) (get histogram-puzzle y 0)))
        set
        (contains? false)
        not)))