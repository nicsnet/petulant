(defproject caas "0.1.0-SNAPSHOT"
  :description "Central Authentication and Authorization Service (CAAS)"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.11"]
            [ragtime/ragtime.lein "0.3.8"]]
  :ring {:handler caas.core/handler}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ragtime "0.3.8"]
                 [ring/ring-core "1.3.2"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [buddy "0.2.3"]
                 [korma "0.4.0"]
                 [http-kit "2.1.19"]
                 [com.taoensso/timbre "3.3.1"]
                 [liberator "0.12.2"]]

   :main caas.core
   :ragtime {:migrations ragtime.sql.files/migrations
             :database (System/getenv "CAAS_DB_URL")})


