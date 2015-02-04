(ns caas.models-test
  (:use midje.sweet
        caas.core-test)
  (:require [caas.models :refer :all]
            [clojure.test :refer :all]
            [environ.core :refer [env]]))

(let [user {:id 42 :email "much@test" :password "suchwow" :user_id 42}])

(facts "add users"
  (with-state-changes [(around :facts (?form (delete-user user)))]
  (fact "add-user! creates a user with password and hashes the password in the db"
    (let [new-user (add-user! user)]

      (:id new-user) => 42
      (:user_id new-user) => 42
      (:email new-user) => "much@test"
      (.contains (:password new-user) "bcrypt+sha512") => true))

  (fact "user-find-by-email finds a user"
  (create user)
  (let [found-user (user-find-by-email (:email user))]

    (:id found-user) => 42
    (:user_id found-user) => 42
    (:email found-user) => "much@test"))))

