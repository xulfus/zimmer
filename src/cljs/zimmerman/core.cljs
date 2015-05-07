(ns zimmerman.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react :as react]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]])
  (:import goog.History))

(defn get-location [] "Tampere")

(defn fetch-weather-data [loc date]
  {:text "Clear"
   :location loc
   :temperature 10.0
   :date date
   :precipitation 0.0
   :icon "http://icons.wxug.com/i/c/k/clear.gif"})

(defn get-weather []
  (go
    (let [resp (<! (http/get "/api/weather/London/2015-05-07"))]
      resp)))

(def weather (atom (fetch-weather-data (get-location) "2015-05-09")))

;(def weather (atom (get-weather)))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Zimmerman"]
   [:div
    [:img {:src (:icon @weather)}]
    [:p "The weather for " (:location @weather) ": " (:text @weather)]
    [:p "Expected precipitation: " (:precipitation @weather) " mm"]
    [:p "Current temperature: " (:temperature @weather) " °C"]
    [:p "Date: " (:date @weather)]

    [:a {:href "#/about"} "about Zimmerman"]]])

(defn about-page []
  [:div [:h2 "About Zimmerman"]
    [:p "You don't need a weatherman to tell which way the wind blows"]
   [:div [:a {:href "#/"} "back to Zimmerman"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
