(ns zimmerman.wunderground
  (:require [clj-xpath.core :as xpath]
            [clj-http.client :as http]
            [zimmerman.util :as util]
            [cemerick.url :as url]
            [clj-time.format :as f]
            [clj-time.core :as t]))

(def api-key (:api-key util/config))

(def date-formatter (f/formatter "YYYY-MM-dd"))

(defn get-raw-weather [loc]
  (let [req-uri (str "http://api.wunderground.com/api/"
                       api-key
                       "/conditions/q/"
                       (url/url-encode loc)  
                       ".xml")]
      (:body (http/get req-uri))))

(defn translate-fields [weather]
  {:text (:weather weather)
   :temp (read-string (:temp_c weather))
   :precipitation (read-string (:precip_today_metric weather))
   :icon (:icon_url weather)})

(defn extract-entities [names xml]
  (zipmap
   (map keyword names)
   (for [name names]
     (xpath/$x:text (str "//" name) xml))))

(defn get-weather-data [xml]
 (-> (extract-entities
      ["weather" "temp_c" "precip_today_metric" "icon_url"]
      xml)
     (translate-fields)
     (assoc :date (f/unparse date-formatter (t/now)))))

(defn get-weather-for [location]
  (let [xml (get-raw-weather location)]
    (get-weather-data xml)))
