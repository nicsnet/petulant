(ns caas.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :as reload]
            [org.httpkit.server :as http-kit]
            [taoensso.timbre :as timbre]
            [compojure.core :refer [defroutes ANY]]))

;; authorized?, then allowed?

(defresource authenticate-user
  :allowed-methods [:get, :post]
  :available-media-types ["text/json"]
  :handle-ok (fn [_] "Hello, Internet"))

(defresource authorize-user [app_name resource_name action token]
  :allowed-methods [:get]
  :available-media-types ["text/json"]
  :handle-ok (fn [_] "Hello, Internet"))

(defroutes app
    (ANY "/authenticate" [] authenticate-user )
    (ANY "/authorize" [app_name resource_name action token] authorize-user)
    (ANY "/configuration" [token]))

(def handler
    (-> app
        wrap-params))

;contains function that can be used to stop http-kit server
(defonce server
  (atom nil))

(defn dev? [args] (some #{"-dev"} args))

(defn parse-port [args]
  (if-let [port (->> args (remove #{"-dev"}) first)]
    (Integer/parseInt port)
    3000))

(defn- start-server [port args]
  (reset! server
          (http-kit/run-server
           (if (dev? args) (reload/wrap-reload app) app)
           {:port port})))

(defn- stop-server []
  (@server))

(defn -main [& args]
  (let [port (parse-port args)]
    (start-server port args)
    (timbre/info "server started on port:" port)))
