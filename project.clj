(defproject caas "0.1.0-SNAPSHOT"
  :description "Central Authentication and Authorization Service (CAAS)"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.11"]
            [lein-environ "1.0.0"]
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
                 [cheshire "5.4.0"]
                 [liberator "0.12.2"]]

  :main caas.core
  :auto-clean false

  ; Have ragtime default to loading the database URL from an environment
  ; variable so that we don't keep production credentials in our source
  ; code but added via Puppet. Note that for our dev environment this needs to be set manually.
  :ragtime {:migrations ragtime.sql.files/migrations
            :database ~(System/getenv "CAAS_DB_URL")}
  :profiles
  {:uberjar {:aot :all}
   :dev {:dependencies [[midje "1.6.3"]
                        [ring-mock "0.1.5"]]
         ; Since we are using environ, we can override these values with
         ; environment variables in production.
         :env {:caas-db ~(System/getenv "CAAS_DB")
               :caas-db-user ~(System/getenv "CAAS_DB_USER")
               :caas-db-pass ~(System/getenv "CAAS_DB_PASS")
               :caas-db-host ~(System/getenv "CAAS_DB_HOST")
               :caas-db-port ~(System/getenv "CAAS_DB_PORT")}}
   :test {:ragtime {:database ~(System/getenv "CAAS_DB_URL_TEST")}
          :dependencies [[midje "1.6.3"]
                         [ring-mock "0.1.5"]]
          :env {:caas-db "caas_test"
                :caas-db-user "caas_test"
                :caas-db-pass "cassonade_test"
                :caas-db-host "localhost"
                :caas-db-port "5432"}}})


