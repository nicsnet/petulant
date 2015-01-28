(ns caas.core
  (:use caas.models)
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :as reload]
            [ring.middleware.session :refer [wrap-session]]
            [org.httpkit.server :as http-kit]
            [taoensso.timbre :as timbre]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.backends.token :refer :all]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.hashers :as hashers]
            [buddy.sign.generic :as sign]
            [buddy.sign.jws :as jws]
            [buddy.core.keys :as ks]
            [clj-time.core :as t]
            [compojure.core :refer [context defroutes ANY routes]]))

;; authorized?, then allowed?

(defresource authenticate-user
  :allowed-methods [:get, :post]
  :available-media-types ["application/json"]
  :exists? (fn [context]
             (let [password (get-in context [:request :params "password"])
                   email (get-in context [:request :params "email"])]
             (if-let [user (user-find-by-email email)]
               (if (hashers/check password (get user :password))
                 {:token (jws/sign (dissoc user :password) "secret")}))))

  :handle-not-found (fn [_] (throw-unauthorized))
  :handle-ok (fn [context] (get context :token)))

(defroutes app
    (ANY "/authenticate" [] authenticate-user))

;; Create an instance of auth backend.

(def auth-backend
  (session-backend))

(def handler
    (-> app
      (wrap-authorization auth-backend)
      (wrap-authentication auth-backend)
      (wrap-params)
      (wrap-session)))

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
