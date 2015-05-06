(ns zimmerman.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react]
              [ajax.core :refer [GET]])
    (:import goog.History))


(defn get-location [] "Tampere")

(defn fetch-weather-data [loc date]
  {:text "Clear"
   :location loc
   :temperature 10.0
   :date date
   :precipitation 0.0
   :icon "http://icons.wxug.com/i/c/k/clear.gif"})

(def weather (atom (fetch-weather-data (get-location) "2015-05-07")))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Zimmerman"]
   [:div
    [:p "You don't need a weatherman to tell which way the wind blows"]
    [:img {:src (:icon @weather)}]
    [:p "The weather for Tampere: " (:text @weather)]
    [:p "Expected precipitation: " (:precipitation @weather) " mm"]
    [:p "Current temperature: " (:temperature @weather) " °C"]
    [:p "Date: " (:date @weather)]

    [:a {:href "#/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About Zimmerman"]
   [:div [:a {:href "#/"} "go to the home page"]]])

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
