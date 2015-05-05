(ns zimmerman.util
  (:require  [clojure.tools.reader.edn :as edn]))

(defn as-resource [path]
  (when path
    (-> (Thread/currentThread) .getContextClassLoader (.getResourceAsStream path))))

(def config 
  (if-let [res (as-resource "config.edn")] 
    (edn/read-string (slurp res))
    ;else
    {:api-key (System/getenv "WG_API_KEY")
     :db-uri (System/getenv "ZM_DB_URI")}))
