(ns zimmerman.api
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.core :refer [defapi]]
            [compojure.core :refer :all]))

(defapi zimmerman
  (context "/api" []
    (GET "/weather/:loc/:date" [loc date]
      {:text "Clear"
       :location "Tampere"
       :temperature 10.0
       :date date
       :precipitation 0.0
       :icon "http://icons.wxug.com/i/c/k/clear.gif"})))
