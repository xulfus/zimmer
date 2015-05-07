(ns zimmerman.weather
  (:require [zimmerman.dada :as dada]
            [zimmerman.wunderground :as wunder]))

(defn- fetch-and-save [loc]
  (let [weather (wunder/get-weather-for loc)]
    (dada/save-weather-data loc weather)
    weather))

(defn fetch-weather [loc date]
  (if-let [weather (first (dada/find-weather-data date loc))]
    weather
    (fetch-and-save loc)))
