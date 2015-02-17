(ns caas.core-test
  (:use midje.sweet
        ring.mock.request
        korma.core)
  (:require [clojure.test :refer :all]
            [caas.models :refer :all]
            [korma.db :as db]
            [cheshire.core :refer :all]
            [caas.core :refer :all]))

(defn parsed-body [response]
  (-> response
      :body
      (parse-string keyword)))

(facts "dummy route to test if the server is responding"
  (fact "it greets us"
    (let [response (app (request :get "/"))]
       (:status response) => 200
       (:body response) => "Hello Internetz.")))

(facts "permissions CRUD"
  (let [user {:id 42 :email "such@doge.de" :password "muchwow" :user_id 42}
        perm {:id 88 :name "suchpermission" :users_id 42}]

   (with-state-changes [(before :facts (add-user! user))(after :facts (delete-user user))(before :facts (delete-permission perm))]

      (fact "GET to /caas/users/:id/permissions return the permissions for a given user"
        (create-permission perm)
          (let [response (app (-> (request :get "/caas/users/42/permissions")))]

            (:status response) => 200
            (parsed-body response) => [perm]))

      (fact "POST to /caas/users/:id/permissions creates a new permission for a given user"
        (let [response (app (-> (request :post "/caas/users/42/permissions")
                                (body (generate-string {:name "suchpermission" :id 88}))
                                (content-type "application/json")
                                (header "Accept" "application/json")))]
            (:status response) => 201
            (parsed-body response) => perm
            (select permissions) => [perm] ))

      (fact "POST to /caas/users/:id/permissions does not create a new permission if this permission already exists for the user"
        (create-permission perm)
        (let [response (app (-> (request :post "/caas/users/42/permissions")
                                (body (generate-string {:name "suchpermission" :id 88}) )
                                (content-type "application/json")
                                (header "Accept" "application/json")))]
            (:status response) => 409
            (:body response) => "Permission already exists, derpy!")
            (select permissions) => [perm])

       (fact "DELETE to /caas/users/:id/permissions deletes a permission for a given user"
         (create-permission perm)
         (let [response (app (-> (request :delete "/caas/users/42/permissions")
                                 (body (generate-string {:name "suchpermission"}))
                                 (content-type "application/json")
                                 (header "Accept" "application/json")))]
           (:status response) => 204
           (select permissions) => []))

       (fact "DELETE to /caas/users/:id/permissions returns a 404 if a permission does not exist for the user"
          (let [response (app (-> (request :delete "/caas/users/108/permissions")
                                  (body (generate-string {:name "nosuchperm"}))
                                  (content-type "application/json")
                                  (header "Accept" "application/json")))]
            (:status response) => 404
            (:body response) => "Resource not found.")))))
