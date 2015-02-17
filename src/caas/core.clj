(ns caas.core
  (:use caas.models liberator.core caas.jwt
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
            [cheshire.core :refer :all]
            [compojure.core :refer [context defroutes ANY GET POST DELETE routes]]))

(defn- json-payload [context]
  (-> context
    (get-in [:request :body])
    slurp
    parse-string))

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
                 {:token (sign (dissoc user :password))}))))

  :handle-not-found throw-unauthorized
  :handle-ok :token
  :post! true
  :handle-created :token)

(defresource authorize-user
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :exists? (fn [context]
             (if-let [token (get-in context [:request :params "token"])]
               (try
                 (unsign token)
                 (catch Exception e (throw-unauthorized)))))

  :handle-not-found throw-unauthorized
  :handle-ok :permissions)

(defresource home
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :handle-ok "Hello Internetz.")

(defresource user-permissions [id]
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :exists? (fn [context] (if-let [user (find-by :user_id (Integer/parseInt id))]
                           {::permissions (:permissions user)}))
  :handle-ok ::permissions)

(defresource create-user-permission [user-id]
  :allowed-methods [:post]
  :available-media-types ["application/json"]
  :post! (fn [context] (if-let [perm (create-permission (conj {"users_id" (Integer/parseInt user-id)} (json-payload context)))]
                          {::resource perm}
                          {::conflict true}))

  :handle-created (fn [context]
                    (if (::conflict context)
                      (ring-response {:status 409 :body "Permission already exists, derpy!"})
                      (::resource context))))

(defresource delete-user-permission [user-id]
  :allowed-methods [:delete]
  :available-media-types ["application/json"]
  :exists? (fn [context] (let [perm-name (get (json-payload context) "name")]
                           (if-let [user-perm (user-permissions-for-name (Integer/parseInt user-id) perm-name)]
                             {::user-perm user-perm })))
  :delete! (comp delete-permission ::user-perm))

(defroutes app
  (ANY "/" [] home)
  (ANY "/caas/authenticate" [] authenticate-user)
  (ANY "/caas/authorize" [] authorize-user)
  (POST "/caas/users/:id/permissions" [id] (create-user-permission id))
  (GET "/caas/users/:id/permissions" [id] (user-permissions id))
  (DELETE "/caas/users/:id/permissions" [id] (delete-user-permission id)))

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
