(ns caas.core
  (:use caas.models liberator.core
        [liberator.representation :only [ring-response]])
 (:require  [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :as reload]
            [ring.middleware.session :refer [wrap-session]]
            [org.httpkit.server :as http-kit]
            [taoensso.timbre :as timbre]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.backends.token :refer :all]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.hashers :as hashers]
            [buddy.sign.generic :refer [sign unsign]]
            [buddy.sign.jws :as jws]
            [buddy.core.keys :as keys]
            [cheshire.core :refer :all]
            [compojure.core :refer [context defroutes ANY GET POST routes]]))

;; create key instances
(def ec-privkey (keys/private-key "ecprivkey.pem"))

(def ec-pubkey (keys/public-key "ecpubkey.pem"))

;; authorized?, then allowed?

(defresource authenticate-user
  :allowed-methods [:get :post]
  :available-media-types ["application/json"]
  :processible? (fn [context] (if-let [email (get-in context [:request :params "email"])] {:email email}))
  :exists? (fn [context]
             (let [password (get-in context [:request :params "password"])
                   email (get-in context [:request :params "email"])]
             (if-let [user (user-find-by-email email)]
               (if (hashers/check password (get user :password))
                 {:token (jws/sign (dissoc user :password) ec-privkey {:alg :es256})}))))

  :handle-not-found (fn [_] (throw-unauthorized))
  :handle-ok (fn [context] (get context :token))
  :post! (fn [_] [true])
  :handle-created (fn [context] (get context :token)))

(defresource authorize-user
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :exists? (fn [context]
             (if-let [token (get-in context [:request :params "token"])]
               (try
                 (jws/unsign token ec-pubkey {:alg :es256})
                 (catch Exception e (throw-unauthorized)))))

  :handle-not-found (fn [_] (throw-unauthorized))
  :handle-ok (fn [context] (get context :permissions)))

(defresource home
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :handle-ok (fn [_] "Hello Internetz."))

(defresource user-permissions [id]
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :exists? (fn [context] (if-let [user (find-by :user_id (Integer/parseInt id))]
                           {:permissions (:permissions user)}))
  :handle-ok (fn [context] (get context :permissions)))

(defresource create-user-permission [user-id]
  :allowed-methods [:post]
  :available-media-types ["application/json"]
  :can-post-to-missing? false
  :post! (fn [context] (let [body (slurp (get-in context [:request :body]))]
                        (if-not (create-permission (parse-string body) )
                            {::conflict true})))
  :handle-created (fn [{conflict ::conflict}]
                    (when conflict
                      (ring-response {:status 409 :body "Permission already exists, derpy!"}))))

(defroutes app
  (ANY "/" [] home)
  (ANY "/caas/authenticate" [] authenticate-user)
  (ANY "/caas/authorize" [] authorize-user)
  (POST "/caas/users/:id/permissions" [id] (create-user-permission id))
  (GET "/caas/users/:id/permissions" [id] (user-permissions id)))

;; Create an instance of auth backend.

(def auth-backend
  (session-backend))

(def handler
    (-> app
      (wrap-params)
      (wrap-authorization auth-backend)
      (wrap-authentication auth-backend)
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
