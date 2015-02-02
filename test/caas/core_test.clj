(ns caas.core-test
  (:use midje.sweet)
  (:require [ring.mock.request :as mock]
            [clojure.test :refer :all]
            [caas.core :refer :all]))

(facts "dummy route to test if the server is responding"
  (fact "it greets us"
    (let [response (app (mock/request :get "/"))]
       (:status response) => 200
       (:body response) => "Hello Internetz.")))

;; (facts "a request to /authenticate authenticates a user and returns a token"
;;   (fact "with incorrect credentials"
;;      (let [response (app (mock/request :get "/authenticate?email=doge@coin.com&password=muchwow"))]
;;         (:status response) => 401
;;         (:body response) => (throws Exception))))
