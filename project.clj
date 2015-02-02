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
                 [ring "1.3.2"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [buddy/buddy-auth "0.3.0"]
                 [buddy/buddy-sign "0.3.0"]
                 [buddy/buddy-hashers "0.3.0"]
                 [korma "0.4.0"]
                 [http-kit "2.1.19"]
                 [com.taoensso/timbre "3.3.1"]
                 [environ "1.0.0"]
                 [liberator "0.12.2"]]

   :main caas.core

   ; Have ragtime default to loading the database URL from an environment
   ; variable so that we don't keep production credentials in our source
   ; code but added via Puppet. Note that for our dev environment this needs to be set manually.
   :ragtime {:migrations ragtime.sql.files/migrations
             :database (System/getenv "CAAS_DB_URL")}
   :profiles
     {:dev {:dependencies [[midje "1.6.3"]
                           [ring-mock "0.1.5"]]}}
     :test {:ragtime {:database "jdbc:postgresql://localhost:15432/caas_test?user=caas_test&password=cassonade_test"}})


