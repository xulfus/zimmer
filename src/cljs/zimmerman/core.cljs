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

(def weather (atom {}))

;; TODO real formatting from cljs.time or goog.string.format
(defn pad-number [num]
 (let [n (str num)]
   (if (< (count n) 2)
     (str "0" n)
     n)))

(defn get-weather [ev]
 (let [loc (.. ev -target -value)
       dt (js/Date.)
       year (.getFullYear dt)
       month (pad-number (inc (.getMonth dt)))
       day (pad-number (.getDate dt))
       dts (str year "-" month "-" day)]
   (go
     (let [resp (<! (http/get (str "/api/weather/" loc "/" dts)))]
       (js/console.log (pr-str resp))
       (reset! weather (assoc (:body resp) :location loc))))))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Zimmerman"]
   [:div
    [:img {:src (:icon @weather)}]
    [:p "The weather for " (:location @weather) ": " (:text @weather)]
    [:p "Expected precipitation: " (:precipitation @weather) " mm"]
    [:p "Current temperature: " (:temp @weather) " °C"]
    [:p "Date: " (:date @weather)]
    [:p
     [:select {:on-change #(get-weather %)} (for [loc ["Tampere Finland" "London UK" "Durham NC"]]
                                              [:option {:key loc} loc])]]
    [:a {:href "#/about"} "about Zimmerman"]]])

(defn about-page []
  [:div [:h2 "About Zimmerman"]
   [:p "You don't need a weatherman to tell which way the wind blows"]
   [:iframe {:width 560 :height 315 :src "https://www.youtube.com/embed/67u2fmYz7S4?rel=0&amp;controls=0&amp;showinfo=0"}]
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
