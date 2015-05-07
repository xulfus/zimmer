(ns zimmerman.api
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer :all]
            [zimmerman.weather :as zim]))

(defapi zimmerman
  (context* "/api" []
           (GET* "/weather/:loc/:date" [loc date]
                (ok (zim/fetch-weather loc date)))))

