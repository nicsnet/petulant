(ns caas.core-test
  (:use midje.sweet
        ring.mock.request)
  (:require [clojure.test :refer :all]
            [caas.models :refer :all]
            [korma.db :as db]
            [cheshire.core :as json]
            [caas.core :refer :all]))

(facts "dummy route to test if the server is responding"
  (fact "it greets us"
    (let [response (app (request :get "/"))]
       (:status response) => 200
       (:body response) => "Hello Internetz.")))

(facts "permissions CRUD"
  (let [user {:id 42 :email "such@doge.de" :password "muchwow" :user_id 42}]

   (with-state-changes [(before :facts (add-user! user))(after :facts (delete-user user))(after :facts (delete-permission perm))]

      (fact "GET to /caas/users/:id/permissions return the permissions for a given user"
        (create-permission {:id 42 :name "suchpermission" :users_id 42})
          (let [response (app (-> (request :get "/caas/users/42/permissions")))]

            (:status response) => 200
            (:body response) => "[{\"name\":\"suchpermission\"}]"))

      (fact "POST to /caas/users/:id/permissions creates a new permission for a given user"))))
