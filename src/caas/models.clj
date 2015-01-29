(ns caas.models
  (:use korma.core korma.db)
  (:require [buddy.hashers :as hashers]))

(defdb db (postgres {:db "caas"
                     :user "caas"
                     :password "cassonade"
                     :host "localhost"
                     :port 15432 }))

(declare users)

(defentity permissions
  (belongs-to users)
  (entity-fields :name))

(defentity users
  (has-many permissions)
  (entity-fields :id :email :password :user_id))

(defn create [user]
  (insert users
    (values user)))

(defn update-user [user]
  (update users
    (set-fields (dissoc user :id))
    (where {:id (user :id)})))

(defn delete-user [user]
    (delete users
    (where {:id (user :id)})))

(defn users-all []
  (select users))

(defn find-by [field value]
  (first
    (select users
      (with permissions)
      (where {field value})
      (limit 1))))

(defn user-find-by-email [email]
  (find-by :email email))

(defn add-user! [user]
    (create (update-in user [:password] #(hashers/encrypt %))))