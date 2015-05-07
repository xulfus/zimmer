(ns zimmerman.dada
  (:require [clojure.java.jdbc :as sql]
            [zimmerman.util :as util]))

(def db-uri (:db-uri util/config))

(defn db-initialized? []
  (-> (sql/query db-uri
                 ["select count(*) from information_schema.tables
                   where table_name='weather'"])
      first
      :count
      pos?))

(defn create-weather-table []
  (sql/db-do-commands
   db-uri
   (sql/create-table-ddl
    :weather
    [:id :serial "PRIMARY KEY"]
    [:text "varchar(50)"]
    [:date "varchar(10)"]
    [:temp "numeric(5,2)"]
    [:precipitation "numeric(5,0)"]
    [:icon "varchar(100)"]
    [:location "varchar(100)"])))

(defn create-tables []
  (create-weather-table))

(defn save-weather-data [location weather]
  (sql/insert! db-uri
               :weather
               (assoc weather :location location)))

(defn find-weather-data [date location]
  (sql/query db-uri
             ["select text, date, temp, precipitation, icon
               from weather where location=? and date=?"
              location
              date]))
