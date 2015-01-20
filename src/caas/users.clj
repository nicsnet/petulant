(ns caas.users
  (:use korma.core)
  (:use korma.db))

(defdb db (postgres {:db "caas"
                     :user "caas"
                     :password "cassonade"
                     :host "localhost"
                     :port 15432 }))

(declare users)

(defentity users
  (pk :id)
  (table :users)
  (entity-fields :email :password :token))

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

