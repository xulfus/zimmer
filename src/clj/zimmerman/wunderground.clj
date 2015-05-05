(ns zimmerman.wunderground
  (:require [clj-xpath.core :as xpath]
            [clj-http.client :as http]
            [zimmerman.util :as util]))

(def api-key (:api-key util/config))

(defn get-raw-weather [loc]
  (let [req-uri (str "http://api.wunderground.com/api/"
                       api-key
                       "/conditions/q/"
                       loc
                       ".xml")]
      (:body (http/get req-uri))))

(defn extract-entities [names xml]
  (apply hash-map
         (interleave
          (map keyword names)
          (for [name names]
            (xpath/$x:text (str "//" name) xml)))))

(defn get-weather [xml]
  (extract-entities
   ["weather" "local_epoch" "temp_c" "precip_today_metric" "icon_url"]
   xml))

(defn get-weather-for [location]
  (let [xml (get-raw-weather location)]
    (get-weather xml)))
