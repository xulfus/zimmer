(ns zimmerman.api
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer :all]))

(defapi zimmerman
  (context* "/api" []
           (GET* "/weather/:loc/:date" [loc date]
                (ok {:text "Clear"
                     :location loc
                     :temperature 10.0
                     :date date
                     :precipitation 0.0
                     :icon "http://icons.wxug.com/i/c/k/clear.gif"}))))

