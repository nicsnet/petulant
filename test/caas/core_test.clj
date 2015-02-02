(ns caas.core-test
  (:use midje.sweet
        ring.mock.request)
  (:require [clojure.test :refer :all]
            [korma.db :as db]
            [cheshire.core :as json]
            [caas.core :refer :all]))

(facts "dummy route to test if the server is responding"
  (fact "it greets us"
    (let [response (app (request :get "/"))]
       (:status response) => 200
       (get-in response [:headers "Content-Type"]) => "application/json;charset=UTF-8"
       (:body response) => "Hello Internetz.")))

(facts "a request to /caas/authenticate authenticates a user and returns a token"
  (fact "with incorrect credentials"
     (let [response (app (-> (request :post "/caas/authenticate")
                             (body "{\"email\":\"doge@coin.com\",\"password\":\"muchwow\"}")
                             (content-type "application/json")
                             (header "Accept" "application/json")))]
        (:status response) => 201
        (:body response) => nil)))
